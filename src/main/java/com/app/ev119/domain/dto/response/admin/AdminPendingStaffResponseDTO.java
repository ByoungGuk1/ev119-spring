package com.app.ev119.domain.dto.response.admin;

import com.app.ev119.domain.type.StaffStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPendingStaffResponseDTO {

    private Long id;

    private Long memberId;
    private String memberName;
    private String memberEmail;


    private String licenseNumber;
    private StaffStatus staffStatus;

}
