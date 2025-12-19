package com.app.ev119.domain.dto.request.member;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {

    private String resetToken;
    private String newPassword;
}
