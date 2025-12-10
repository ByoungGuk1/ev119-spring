package com.app.ev119.api.publicApi;

import com.app.ev119.domain.dto.ApiResponseDTO;
import com.app.ev119.domain.dto.request.TraumaCenterLocationInfoRequestDTO;
import com.app.ev119.domain.dto.response.TraumaCenterLocationInfoResponse;
import com.app.ev119.service.TraumaCenterLocationInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emergency")
public class TraumaCenterLocationInfoApi {

    private final TraumaCenterLocationInfoService traumaCenterLocationInfoService;
    private final RestClient.Builder builder;

    @GetMapping("/trauma-centers")
    public ResponseEntity<ApiResponseDTO<TraumaCenterLocationInfoResponse>> getTraumaCenterLocationInfo(
            @RequestParam("lon") double lon,
            @RequestParam("lat") double lat,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "numOfRows", defaultValue = "10") Integer numOfRows
            ) {

        TraumaCenterLocationInfoRequestDTO req = new TraumaCenterLocationInfoRequestDTO();
        req.setWgs84Lon(lon);
        req.setWgs84Lat(lat);
        req.setPageNo(pageNo);
        req.setNumOfRows(numOfRows);

        TraumaCenterLocationInfoResponse response = traumaCenterLocationInfoService.getTraumaCenterLocationInfo(req);



        return ResponseEntity.ok(ApiResponseDTO.of("SUCCESS", response));
    }

}
