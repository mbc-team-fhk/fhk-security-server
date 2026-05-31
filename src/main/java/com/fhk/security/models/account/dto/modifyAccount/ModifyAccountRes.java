package com.fhk.security.models.account.dto.modifyAccount;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModifyAccountRes {
    private Long id;
    private String nickname;
}
