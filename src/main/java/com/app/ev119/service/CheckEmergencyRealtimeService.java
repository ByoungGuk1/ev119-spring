package com.app.ev119.service;

import com.app.ev119.domain.dto.request.CheckEmergencyRealtimeRequestDTO;
import com.app.ev119.domain.dto.response.CheckEmergencyRealtimeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckEmergencyRealtimeService {

    private final RestTemplate restTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${api.emergency.service-key}")
    private String serviceKey;

    @Value("${api.emergency.check-emergency-url}")
    private String checkEmergencyRealtimeUrl;

    // 429 뜨면 같은 요청을 잠깐 막아서(재시도 폭주 방지) 쿼터 보호
    private static final Duration QUOTA_BLOCK_TTL = Duration.ofSeconds(180);
    private static final String QUOTA_BLOCK_PREFIX = "emergency:quota:block:";

    /**
     * ✅ 캐시 포인트
     * - stage1, stage2, pageNo, numOfRows 가 같으면 캐시로 반환
     * - 좌표 검색 서비스에서 같은 stage 조합을 여러 번 호출하는 구조에서 쿼터 방어에 매우 효과적
     */
    @Cacheable(
            cacheNames = "emergency:realtime",
            key = "T(String).format('%s|%s|p=%d|r=%d', " +
                    "T(org.springframework.util.StringUtils).trimAllWhitespace(#req.stage1), " +
                    "T(org.springframework.util.StringUtils).trimAllWhitespace(#req.stage2), " +
                    "#req.pageNo, #req.numOfRows)"
    )
    public CheckEmergencyRealtimeResponse getCheckEmergencyRealtimeResponse(CheckEmergencyRealtimeRequestDTO req) {

        String stage1 = safeTrim(req.getStage1());
        String stage2 = safeTrim(req.getStage2());
        int pageNo = Objects.requireNonNullElse(req.getPageNo(), 1);
        int numOfRows = Objects.requireNonNullElse(req.getNumOfRows(), 10);

        // ✅ 429 발생 후 잠깐 블록 (같은 조합 재호출 방지)
        String quotaBlockKey = QUOTA_BLOCK_PREFIX + stage1 + "|" + stage2;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(quotaBlockKey))) {
            throw new IllegalStateException("공공 API 서비스키 쿼터 초과로 잠시 호출이 제한되었습니다. 잠시 후 다시 시도해 주세요.");
        }

        String url = UriComponentsBuilder
                .fromHttpUrl(checkEmergencyRealtimeUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("STAGE1", stage1)
                .queryParam("STAGE2", stage2)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .encode(StandardCharsets.UTF_8)
                .build(false)
                .toUriString();

        log.info("[CheckEmergencyRealtime] URL = {}", url);

        try {
            // ✅ 중복 호출 제거: 딱 1번만 호출
            CheckEmergencyRealtimeResponse response =
                    restTemplate.getForObject(url, CheckEmergencyRealtimeResponse.class);

            // 너무 큰 RAW XML 로그는 서버 터질 수 있어서 기본은 비추
            // 필요하면 response header(결과코드/메시지/totalCount)만 찍는 게 안전
            return response;

        } catch (HttpClientErrorException e) {
            // ✅ 429 Too Many Requests (쿼터 초과)
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                stringRedisTemplate.opsForValue().set(quotaBlockKey, "1", QUOTA_BLOCK_TTL);
                log.warn("[CheckEmergencyRealtime] QUOTA_EXCEEDED(429). key={}, ttl={}s",
                        quotaBlockKey, QUOTA_BLOCK_TTL.getSeconds());
                throw new IllegalStateException("공공 API 서비스키 쿼터 초과(429)입니다. 잠시 후 다시 시도해 주세요.");
            }

            log.error("[CheckEmergencyRealtime] HTTP ERROR status={}, body={}",
                    e.getStatusCode(), safeBody(e.getResponseBodyAsString()));
            throw e;
        } catch (Exception e) {
            log.error("[CheckEmergencyRealtime] ERROR stage1={}, stage2={}, p={}, r={}",
                    stage1, stage2, pageNo, numOfRows, e);
            throw e;
        }
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private String safeBody(String body) {
        if (body == null) return "";
        // 로그 폭주 방지용 (너무 길면 잘라서 출력)
        return body.length() > 1000 ? body.substring(0, 1000) + "...(truncated)" : body;
    }
}
