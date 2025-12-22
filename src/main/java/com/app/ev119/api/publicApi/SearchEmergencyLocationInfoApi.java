package com.app.ev119.api.publicApi;

import com.app.ev119.domain.dto.ApiResponseDTO;
import com.app.ev119.domain.dto.request.SearchEmergencyLocationInfoRequestDTO;
import com.app.ev119.domain.dto.response.SearchEmergencyLocationInfoResponse;
import com.app.ev119.service.SearchEmergencyLocationInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emergency")
public class SearchEmergencyLocationInfoApi {

    private final SearchEmergencyLocationInfoService searchEmergencyLocationInfoService;

    @GetMapping("/search-emergency")
    public ResponseEntity<ApiResponseDTO<SearchEmergencyLocationInfoResponse>> getSearchEmergencyLocationInfo(
            @RequestParam("lon") double lon,
            @RequestParam("lat") double lat,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "numOfRows", defaultValue = "10") Integer numOfRows
    ) {
        SearchEmergencyLocationInfoRequestDTO req = new SearchEmergencyLocationInfoRequestDTO();
        req.setWgs84Lon(lon);
        req.setWgs84Lat(lat);
        req.setPageNo(pageNo);
        req.setNumOfRows(numOfRows);

        SearchEmergencyLocationInfoResponse response =
                searchEmergencyLocationInfoService.getSearchEmergencyLocationInfo(req);

        return ResponseEntity.ok(ApiResponseDTO.of("success", response));
    }

    @GetMapping("/search-emergency-with-status")
    public ResponseEntity<ApiResponseDTO<SearchEmergencyLocationInfoResponse>> getSearchEmergencyWithStatus(
            @RequestParam("lon") double lon,
            @RequestParam("lat") double lat,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "numOfRows", defaultValue = "10") Integer numOfRows
    ) {
        SearchEmergencyLocationInfoRequestDTO req = new SearchEmergencyLocationInfoRequestDTO();
        req.setWgs84Lon(lon);
        req.setWgs84Lat(lat);
        req.setPageNo(pageNo);
        req.setNumOfRows(numOfRows);

        SearchEmergencyLocationInfoResponse response =
               searchEmergencyLocationInfoService.getSearchEmergencyLocationInfoWithStatus(req);

        // ✅ 서비스 내부에서 429면 base만 반환하도록 했으니, 여긴 200 유지
        // 메시지 구분은 필요하면 아래처럼 바꾸면 됨(선택)
        return ResponseEntity.ok(ApiResponseDTO.of("success", response));
    }
}
