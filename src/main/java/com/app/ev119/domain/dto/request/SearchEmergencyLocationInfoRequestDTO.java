package com.app.ev119.domain.dto.request;

import lombok.Data;

@Data
public class SearchEmergencyLocationInfoRequestDTO {
//    응급의료기관 위치정보 조회

    private double wgs84Lon; // 병원 경도
    private double wgs84Lat; // 병원 위도
    private Integer PageNo; // 페이지 번호
    private Integer numOfRows; // 목록 건수
}
