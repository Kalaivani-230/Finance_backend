package com.finance.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String newPassword;
}
