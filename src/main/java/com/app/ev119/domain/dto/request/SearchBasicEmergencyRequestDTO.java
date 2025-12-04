package com.app.ev119.domain.dto.request;

import lombok.Data;

@Data
public class SearchBasicEmergencyRequestDTO {
//    응급의료기관 기본정보 조회

    private String hpid; //기관 ID
    private Integer pageNo; // 페이지 번호
    private Integer numOfRows; // 목록 건수
}
