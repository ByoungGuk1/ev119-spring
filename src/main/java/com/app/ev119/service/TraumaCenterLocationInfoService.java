package com.app.ev119.service;

import com.app.ev119.domain.dto.request.TraumaCenterLocationInfoRequestDTO;
import com.app.ev119.domain.dto.response.TraumaCenterLocationInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class TraumaCenterLocationInfoService {

    private final RestTemplate restTemplate;

    @Value("${api.emergency.service-key}")
    private String serviceKey;

    @Value("${api.emergency.trauma-location-url}")
    private String traumaUrl;

    public TraumaCenterLocationInfoResponse getTraumaCenterLocationInfo(TraumaCenterLocationInfoRequestDTO req){
        String url = UriComponentsBuilder
                .fromHttpUrl(traumaUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", req.getPageNo())
                .queryParam("numOfRows", req.getNumOfRows())
                .queryParam("WGS84_LON", req.getWgs84Lon())
                .queryParam("WGS84_LAT", req.getWgs84Lat())
                .toUriString();

        return restTemplate.getForObject(url, TraumaCenterLocationInfoResponse.class);
    }

}
