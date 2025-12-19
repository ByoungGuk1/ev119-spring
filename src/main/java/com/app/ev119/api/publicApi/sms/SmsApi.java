package com.app.ev119.api.publicApi.sms;

import com.app.ev119.service.sms.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
@Slf4j
public class SmsApi {

    private final SmsService smsService;

    @PostMapping("/send")
    public ResponseEntity<?> sendAuthCode(@RequestParam String memberPhone) {
        try{
            smsService.sendAuthCode(memberPhone);
            log.info("인증번호 발송 요청 완료 - to:{}",  memberPhone);
            return ResponseEntity.ok().body(Map.of("message", "인증번호가 발송되었습니다."));
        } catch (Exception e){
            log.error("인증번호 발송 요청 실패 - to:{}, 에러:{}", memberPhone, e.getMessage(), e);
            return ResponseEntity.badRequest().body("SMS 발송에 실패했습니다" + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyAuthCode(@RequestParam String memberPhone, @RequestParam String authCode){
        boolean result = false;
        try {
            result = smsService.verifyAUthCode(memberPhone, authCode);
        } catch (Exception e) {
            log.error("인증번호 검증 중 예외 - to:{}, 에러:{}",  memberPhone, e.getMessage());
            return ResponseEntity.internalServerError().body("인증 중 서버 오류: " + e.getMessage());
        }

        if(result) {
            log.info("인증번호 검증 성공 - to:{}", memberPhone);
            return ResponseEntity.ok().body(Map.of("message", "인증되었습니다."));
        } else {
            log.warn("인증번호 검증 실패 - to:{}", memberPhone);
            return ResponseEntity.badRequest().body(Map.of("message", "인증번호가 일치하지 않습니다."));
        }
    }
}
