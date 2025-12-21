package com.app.ev119.domain.dto.response;

import lombok.*;

@Getter @Setter @ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO {
    private String currentPassword;
    private String newPassword;
}
