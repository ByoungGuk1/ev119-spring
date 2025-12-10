package com.app.ev119.api.privateApi;

import com.app.ev119.domain.dto.ApiResponseDTO;
import com.app.ev119.domain.entity.Allergy;
import com.app.ev119.domain.entity.Member;
import com.app.ev119.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @Slf4j
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class AllergyApi {
    private final MyPageService myPageService;
//    알레르기 정보
    @GetMapping("/allergy")
    public ResponseEntity<ApiResponseDTO> getAllergy(@RequestBody Member member) {

        List<Allergy> allergiesByMember = myPageService.findAllergyByMember(member);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDTO.of("멤버의 알러지 정보가져오기 성공",allergiesByMember));
    }
}
