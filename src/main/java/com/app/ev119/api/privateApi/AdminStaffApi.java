package com.app.ev119.api.privateApi;

import com.app.ev119.domain.dto.ApiResponseDTO;
import com.app.ev119.domain.dto.response.admin.AdminPendingStaffResponseDTO;
import com.app.ev119.domain.entity.MemberStaff;
import com.app.ev119.domain.type.StaffStatus;
import com.app.ev119.repository.MemberStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Transactional
public class AdminStaffApi {

    private final MemberStaffRepository memberStaffRepository;

    @GetMapping("/staff/pending")
    public ResponseEntity<ApiResponseDTO> pendingStaff() {

        List<AdminPendingStaffResponseDTO> pendingList = memberStaffRepository.findAll().stream()
                .filter(ms -> ms.getStaffStatus() == StaffStatus.PENDING)
                .map(ms -> AdminPendingStaffResponseDTO.builder()
                        .id(ms.getId())
                        .staffStatus(ms.getStaffStatus())
                        .licenseNumber(ms.getLicenseNumber())
                        .memberId(ms.getMember().getId())
                        .memberName(ms.getMember().getMemberName())
                        .memberEmail(ms.getMember().getMemberEmail())
                        .build())
                .toList();

        return ResponseEntity.ok(ApiResponseDTO.of("의료진 승인 대기 목록", pendingList));
    }

    @GetMapping("/staff")
    public ResponseEntity<ApiResponseDTO> staffList(
            @RequestParam(value = "status", required = false) StaffStatus status
    ) {
        List<AdminPendingStaffResponseDTO> list = memberStaffRepository.findAll().stream()
                .filter(ms -> status == null || ms.getStaffStatus() == status)
                .map(ms -> AdminPendingStaffResponseDTO.builder()
                        .id(ms.getId())
                        .staffStatus(ms.getStaffStatus())
                        .licenseNumber(ms.getLicenseNumber())
                        .memberId(ms.getMember().getId())
                        .memberName(ms.getMember().getMemberName())
                        .memberEmail(ms.getMember().getMemberEmail())
                        .build())
                .toList();

        return ResponseEntity.ok(ApiResponseDTO.of("의료진 신청 목록", list));
    }



    @PatchMapping("/staff/{memberStaffId}/approve")
    public ResponseEntity<ApiResponseDTO> approveStaff(@PathVariable Long memberStaffId) {

        MemberStaff staff = memberStaffRepository.findById(memberStaffId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "의료진 신청 정보가 없습니다."));

        staff.setStaffStatus(StaffStatus.APPROVED);

        return ResponseEntity.ok(ApiResponseDTO.of("의료진 승인 완료"));
    }

    @PatchMapping("/staff/{memberStaffId}/reject")
    public ResponseEntity<ApiResponseDTO> rejectStaff(@PathVariable Long memberStaffId) {

        MemberStaff staff = memberStaffRepository.findById(memberStaffId)
                .orElseThrow(() -> new IllegalArgumentException("의료진 신청 정보가 없습니다."));

        staff.setStaffStatus(StaffStatus.REJECTED);

        return ResponseEntity.ok(
                ApiResponseDTO.of("의료진 신청이 반려되었습니다.")
        );
    }
}
