package com.fhk.security.models.account.dto.modifyAccount;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ModifyAccountReq {
    @NotBlank(message = "password is required")
    @Size(min = 8, max = 24, message = "password must be between 8 and 24 characters")
    private String password;

    @NotBlank(message = "nickname is required")
    @Size(min = 2, max = 16, message = "nickname must be between 2 and 16 characters")
    private String nickname;
}
