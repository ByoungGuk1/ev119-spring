package com.app.ev119.domain.dto.request;

import lombok.Data;

@Data
public class CheckAvailabilityIllPatientsRequestDTO {
// 중증질환자 수용가능정보 조회

    private String stage1; // 시 도
    private String stage2; // 시 군 구
    private String smTown; // 중증질환 가능한 병원 찾기
    private Integer pageNo; // 페이지 번호
    private Integer numOfRows; // 목록 건수
}
