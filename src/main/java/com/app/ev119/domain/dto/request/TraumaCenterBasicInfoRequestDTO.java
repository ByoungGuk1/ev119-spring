package com.app.ev119.domain.dto.request;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class TraumaCenterBasicInfoRequestDTO {
//  외상센터 기본정보 조회
    @Value("${api.emergency.service-key}")
    private String serviceKey; // 인증키

    private Integer pageNo; // 페이지 수
    private Integer numOfRows; // 목록 건수
    private String hpid; // 기관ID
    private String qn; // 기관명
}
