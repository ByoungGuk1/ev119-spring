package com.app.ev119.config;

import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsConfig {

    @Value("${coolsms.api-key}")
    private String apiKey;

    @Value("${coolsms.api-secret}")
    private String apiSecret;

    @Bean
    public DefaultMessageService messageService(){
        return new DefaultMessageService(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }
}
