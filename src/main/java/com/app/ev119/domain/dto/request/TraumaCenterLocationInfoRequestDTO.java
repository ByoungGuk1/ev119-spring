package com.app.ev119.domain.dto.request;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class TraumaCenterLocationInfoRequestDTO {
//  외상센터 위치정보 조회

    private Integer pageNo; // 페이지 수
    private Integer numOfRows; // 목록 건수
    private double wgs84Lon; // 병원 경도 
    private double wgs84Lat; // 병원 위도
}
