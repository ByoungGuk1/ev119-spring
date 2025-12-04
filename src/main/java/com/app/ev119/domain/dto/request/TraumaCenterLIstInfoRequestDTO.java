package com.app.ev119.domain.dto.request;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class TraumaCenterLIstInfoRequestDTO {
//    외상센터 목록정보 조회

    @Value("${api.emergency.service-key}")
    private String serviceKey; // 인증키

    private Integer pageNo; // 페이지 번호
    private Integer numOfRows; // 목록 건수
    private String q0; //시 도
    private String q1; // 시 군 구
    private String qt; // 월~일요일(1~7), 공휴일(8)
    private String qz; // CODE_MST의 'H000'
    private String qd;  // CODE_MST의 'D000'
    private String qn; // 기관명
    private String ord; // 순서
}
