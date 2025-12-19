package com.app.ev119.util;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SmsUtil {

    public static String generateAuthCode() {
        Random random = new Random();
        int code =  100000 + random.nextInt(90000);
        return String.valueOf(code);
    }

    public String makeAuthMessage(String authCode){
        return "[IssueMate 인증번호]" + authCode + "\n본인 확인을 위해 인증번호를 입력해주세요.";
    }
}
