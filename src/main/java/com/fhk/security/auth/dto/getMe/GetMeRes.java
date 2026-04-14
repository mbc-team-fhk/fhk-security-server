package com.fhk.security.auth.dto.getMe;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetMeRes {
    private Long accountId;
    private String loginId;
    private String nickname;
    private String role;
}
