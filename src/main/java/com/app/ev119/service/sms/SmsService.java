package com.app.ev119.service.sms;

import com.app.ev119.util.SmsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    private final DefaultMessageService messageService;
    private final SmsUtil smsUtil;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String SMS_AUTH_PREFIX = "SMS:AUTH:";
    private static final String RESET_PW_PREFIX = "RESET_PW:";

    public void sendAuthCode(String memberPhone) {
        String authCode = SmsUtil.generateAuthCode();
        String messageText = smsUtil.makeAuthMessage(authCode);

        Message message = new Message();
        message.setFrom("010-5705-1641");
        message.setTo(memberPhone);
        message.setText(messageText);

        try {
            messageService.sendOne(new SingleMessageSendingRequest(message));
            log.info("인증번호 발송 - to: {}, code: {}", memberPhone, authCode);
        } catch (Exception e) {
            log.error("인증번호 발송 실패 - to: {}, 에러:{}", memberPhone, e.getMessage(), e);
            throw new RuntimeException("Sms 인증에 실패했습니다.");
        }

        String redisKey = SMS_AUTH_PREFIX + memberPhone;
        stringRedisTemplate.opsForValue().set(redisKey, authCode, 3, TimeUnit.MINUTES);
        log.info("SMS 인증번호 Redis 저장 - key:{}, code:{}", redisKey, authCode);
    }

    public boolean verifyAUthCode(String memberPhone, String inputCode) {
        String redisKey = SMS_AUTH_PREFIX + memberPhone;

        String storedCode = stringRedisTemplate.opsForValue().get(redisKey);
        log.info("인증번호 검증 요청 - key:{}, 입력값:{}, 저장값:{}", redisKey, inputCode, storedCode);

        if (storedCode == null) {
            log.warn("인증번호 없음(만료/미요청) - key:{}, 입력값:{}", redisKey, inputCode);
            return false;
        }

        if (storedCode.equals(inputCode)) {
            stringRedisTemplate.delete(redisKey);
            log.info("인증번호 검증 성공 - key:{}", redisKey);
            return true;
        }

        log.warn("인증번호 불일치 - key:{}, 입력값:{}", redisKey, inputCode);
        return false;
    }

    public String verifyAndIssueResetToken(String authCode, String memberPhone) {

        boolean ok = verifyAUthCode(memberPhone, authCode);

        if (!ok) {
            throw new IllegalArgumentException("인증번호가 일치하지 않거나 만료되었습니다.");
        }

        String resetToken = java.util.UUID.randomUUID().toString().replace("-", "");

        String key = RESET_PW_PREFIX + resetToken;
        stringRedisTemplate.opsForValue().set(key, memberPhone, 10, TimeUnit.MINUTES);

        log.info("비밀번호 재설정 토큰 발급 - phone:{}, key:{}, ttl:{}min", memberPhone, key, 10);

        return resetToken;
    }

}


