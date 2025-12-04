package com.app.ev119.domain.dto.request;

import lombok.Data;

@Data
public class CheckEmergencyRealtimeRequestDTO {
//    응급실 실시간 가용병상정보 조회
    private String stage1; //시 도
    private String stage2; //시 군 구
    private Integer pageNo; //페이지 번호
    private Integer numOfRows; // 목록 건수
}
