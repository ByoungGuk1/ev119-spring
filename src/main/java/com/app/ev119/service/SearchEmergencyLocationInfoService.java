package com.app.ev119.service;

import com.app.ev119.domain.dto.request.CheckEmergencyRealtimeRequestDTO;
import com.app.ev119.domain.dto.request.SearchEmergencyLocationInfoRequestDTO;
import com.app.ev119.domain.dto.response.CheckEmergencyRealtimeItem;
import com.app.ev119.domain.dto.response.CheckEmergencyRealtimeResponse;
import com.app.ev119.domain.dto.response.SearchEmergencyLocationInfoItem;
import com.app.ev119.domain.dto.response.SearchEmergencyLocationInfoResponse;
import com.app.ev119.exception.QuotaExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchEmergencyLocationInfoService {

    private final RestTemplate restTemplate;
    private final CheckEmergencyRealtimeService checkEmergencyRealtimeService;

    @Value("${api.emergency.service-key}")
    private String serviceKey;

    @Value("${api.emergency.search-emergency-location-url}")
    private String searchEmergencyLocationUrl;

    // ✅ 한 요청에서 실시간 API를 너무 많이 치지 않도록 상한(권장 3~5)
    private static final int REALTIME_STAGEPAIR_LIMIT = 5;

    public SearchEmergencyLocationInfoResponse getSearchEmergencyLocationInfo(SearchEmergencyLocationInfoRequestDTO req) {
        String url = UriComponentsBuilder
                .fromHttpUrl(searchEmergencyLocationUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("WGS84_LON", req.getWgs84Lon())
                .queryParam("WGS84_LAT", req.getWgs84Lat())
                .queryParam("pageNo", req.getPageNo())
                .queryParam("numOfRows", req.getNumOfRows())
                .toUriString();

        return restTemplate.getForObject(url, SearchEmergencyLocationInfoResponse.class);
    }

    /**
     * ✅ 목표:
     * - 실시간 포화 API가 429(쿼터초과) 나면 더 때리지 않고 즉시 중단
     * - 그 경우에도 base(주변 병원) 목록은 화면에 보여야 하므로 base 응답 그대로 반환
     */
    public SearchEmergencyLocationInfoResponse getSearchEmergencyLocationInfoWithStatus(SearchEmergencyLocationInfoRequestDTO req) {

        SearchEmergencyLocationInfoResponse base = getSearchEmergencyLocationInfo(req);
        if (base == null || base.getBody() == null || base.getBody().getItems() == null || base.getBody().getItems().isEmpty()) {
            log.info("[MERGE] base is empty (no items).");
            return base;
        }

        List<SearchEmergencyLocationInfoItem> items = base.getBody().getItems();

        // 1) 주소에서 stage1/stage2 후보 뽑기
        LinkedHashSet<String> stagePairs = new LinkedHashSet<>();
        for (SearchEmergencyLocationInfoItem it : items) {
            Stage st = extractStage1Stage2(it.getDutyAddr());
            if (st.stage1 != null && st.stage2 != null) {
                stagePairs.add(st.stage1 + "||" + st.stage2);
            }
        }

        if (stagePairs.isEmpty()) {
            log.info("[MERGE] stagePairs empty.");
            return base;
        }

        Map<String, CheckEmergencyRealtimeItem> rtMap = new HashMap<>();

        // ✅ 실시간 API 호출: 429 뜨면 즉시 폴백(base 반환)
        try {
            int count = 0;
            for (String pair : stagePairs) {
                if (count >= REALTIME_STAGEPAIR_LIMIT) break;

                String[] p = pair.split("\\|\\|");
                if (p.length < 2) continue;

                String stage1 = normalizeStage1(p[0]);
                String stage2 = normalizeStage2(p[1]);

                if (stage1 == null || stage2 == null) continue;

                mergeRealtimeWithFallback(stage1, stage2, rtMap);
                count++;
            }
        } catch (QuotaExceededException e) {
            log.warn("[MERGE-FALLBACK] realtime quota exceeded -> return base only. msg={}", e.getMessage());
            return base;
        }

        // 3) 병원별 매칭해서 hvec/hvgc 채우기
        for (SearchEmergencyLocationInfoItem it : items) {
            if (it.getHpid() == null) continue;

            String key = normalizeKey(it.getHpid());
            CheckEmergencyRealtimeItem rti = rtMap.get(key);

            if (rti != null) {
                it.setHvec(rti.getHvec());
                it.setHvgc(rti.getHvgc());
            }
        }

        long matched = items.stream()
                .filter(x -> x.getHvec() != null && !x.getHvec().trim().isEmpty())
                .count();

        log.info("[MERGE] totalItems={}, matchedHvec={}", items.size(), matched);
        log.info("[MERGE] stagePairs.size={}, limited={}", stagePairs.size(), REALTIME_STAGEPAIR_LIMIT);
        stagePairs.stream().limit(10).forEach(p -> log.info("[MERGE] pair={}", p));
        log.info("[MERGE] rtMap.size={}", rtMap.size());

        return base;
    }

    private static class Stage {
        String stage1;
        String stage2;

        Stage(String s1, String s2) {
            this.stage1 = s1;
            this.stage2 = s2;
        }
    }

    private String normalizeStage1(String stage1) {
        if (stage1 == null) return null;
        String s = stage1.trim();
        if (s.isEmpty()) return null;

        if (s.equals("서울특별시")) return "서울";
        if (s.equals("부산광역시")) return "부산";
        if (s.equals("대구광역시")) return "대구";
        if (s.equals("인천광역시")) return "인천";
        if (s.equals("광주광역시")) return "광주";
        if (s.equals("대전광역시")) return "대전";
        if (s.equals("울산광역시")) return "울산";
        if (s.equals("세종특별자치시")) return "세종";
        if (s.equals("제주특별자치도")) return "제주";
        if (s.equals("경기도")) return "경기";
        if (s.equals("강원특별자치도") || s.equals("강원도")) return "강원";
        if (s.equals("충청북도")) return "충북";
        if (s.equals("충청남도")) return "충남";
        if (s.equals("전라북도")) return "전북";
        if (s.equals("전라남도")) return "전남";
        if (s.equals("경상북도")) return "경북";
        if (s.equals("경상남도")) return "경남";

        return s;
    }

    private String normalizeStage2(String stage2) {
        if (stage2 == null) return null;
        String s = stage2.trim();
        if (s.isEmpty()) return null;

        s = s.replaceAll("[,()]", "").trim();
        s = s.replaceAll("\\s+", " ").trim();

        return s.isEmpty() ? null : s;
    }

    private String normalizeKey(String s) {
        if (s == null) return null;
        return s.replace('\u00A0', ' ')
                .replaceAll("\\s+", "")
                .trim();
    }

    private Stage extractStage1Stage2(String dutyAddr) {
        if (dutyAddr == null) return new Stage(null, null);
        String a = dutyAddr.trim();
        if (a.isEmpty()) return new Stage(null, null);

        String[] parts = a.split("\\s+");
        if (parts.length < 2) return new Stage(null, null);

        String stage1 = parts[0].trim();
        String stage2 = parts[1].trim();

        // "수원시 영통구" 같이 시/군 + 구 형태면 합쳐주기
        if (parts.length >= 3) {
            String p2 = parts[1].trim();
            String p3 = parts[2].trim();
            if ((p2.endsWith("시") || p2.endsWith("군")) && p3.endsWith("구")) {
                stage2 = p2 + " " + p3;
            }
        }

        return new Stage(stage1, stage2);
    }

    private void mergeRealtimeWithFallback(String stage1, String stage2, Map<String, CheckEmergencyRealtimeItem> rtMap) {
        List<String> candidates = buildStage2Candidates(stage2);

        for (String cand : candidates) {
            try {
                CheckEmergencyRealtimeResponse first = fetchRealtimeFirstPage(stage1, cand, 1, 500);
                int totalCount = safeTotalCount(first);

                log.info("[RT-TRY] stage1='{}', stage2='{}', totalCount={}", stage1, cand, totalCount);

                if (totalCount > 0) {
                    mergeRealtimeAllPages(stage1, cand, rtMap);
                    return;
                }
            } catch (QuotaExceededException e) {
                // ✅ 429면 후보(stage2) 바꿔가며 재시도 금지. 즉시 상위로 전파해서 base 반환하게 만들기
                log.warn("[RT-EX] quota exceeded while trying stage1='{}', stage2='{}' -> stop all realtime", stage1, cand);
                throw e;
            }
        }

        log.info("[RT-FAIL] stage1='{}', stage2(original)='{}' -> all candidates empty", stage1, stage2);
    }

    private List<String> buildStage2Candidates(String stage2) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (stage2 == null) return new ArrayList<>();

        String s = stage2.trim().replaceAll("\\s+", " ");
        if (s.isEmpty()) return new ArrayList<>();

        set.add(s);

        if (s.contains(" ")) {
            set.add(s.split("\\s+")[0]);
        }

        String stripped = s.replaceAll("(특별시|광역시|자치시|시|군|구)$", "").trim();
        if (!stripped.isEmpty() && !stripped.equals(s)) {
            set.add(stripped);
        }

        if (s.contains(" ")) {
            String[] arr = s.split("\\s+");
            if (arr.length >= 2) set.add(arr[arr.length - 1]);
        }

        List<String> more = set.stream()
                .map(x -> x.replaceAll("(시|군|구)$", "").trim())
                .filter(x -> !x.isEmpty())
                .collect(Collectors.toList());
        set.addAll(more);

        return new ArrayList<>(set);
    }

    private void mergeRealtimeAllPages(String stage1, String stage2, Map<String, CheckEmergencyRealtimeItem> rtMap) {
        int page = 1;
        int numOfRows = 500;
        int totalCount = Integer.MAX_VALUE;

        while ((page - 1) * numOfRows < totalCount) {
            CheckEmergencyRealtimeRequestDTO rtReq = new CheckEmergencyRealtimeRequestDTO();
            rtReq.setStage1(stage1);
            rtReq.setStage2(stage2);
            rtReq.setPageNo(page);
            rtReq.setNumOfRows(numOfRows);

            CheckEmergencyRealtimeResponse rt = checkEmergencyRealtimeService.getCheckEmergencyRealtimeResponse(rtReq);

            if (rt == null || rt.getBody() == null || rt.getBody().getItems() == null) {
                log.info("[RT] stage1='{}', stage2='{}' page={} -> response null/body/items null", stage1, stage2, page);
                break;
            }

            totalCount = rt.getBody().getTotalCount();
            List<CheckEmergencyRealtimeItem> list = rt.getBody().getItems();
            if (list.isEmpty()) {
                log.info("[RT] stage1='{}', stage2='{}' page={} -> items empty", stage1, stage2, page);
                break;
            }

            for (CheckEmergencyRealtimeItem rti : list) {
                if (rti.getHpid() == null) continue;
                rtMap.putIfAbsent(normalizeKey(rti.getHpid()), rti);
            }

            page++;
            if (page > 30) break; // 안전장치
        }
    }

    private CheckEmergencyRealtimeResponse fetchRealtimeFirstPage(String stage1, String stage2, int pageNo, int numOfRows) {
        try {
            CheckEmergencyRealtimeRequestDTO rtReq = new CheckEmergencyRealtimeRequestDTO();
            rtReq.setStage1(stage1);
            rtReq.setStage2(stage2);
            rtReq.setPageNo(pageNo);
            rtReq.setNumOfRows(numOfRows);

            return checkEmergencyRealtimeService.getCheckEmergencyRealtimeResponse(rtReq);

        } catch (QuotaExceededException e) {
            // ✅ 여기서 삼키면 "0건"처럼 보여서 후보 재시도 루프가 돈다 -> 절대 삼키지 말고 throw
            log.warn("[RT-429] QUOTA EXCEEDED stage1='{}', stage2='{}' -> {}", stage1, stage2, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.warn("[RT-EX] stage1='{}', stage2='{}' -> {}", stage1, stage2, e.getMessage());
            return null;
        }
    }

    private int safeTotalCount(CheckEmergencyRealtimeResponse rt) {
        try {
            if (rt == null || rt.getBody() == null) return 0;
            return rt.getBody().getTotalCount();
        } catch (Exception e) {
            return 0;
        }
    }
}
