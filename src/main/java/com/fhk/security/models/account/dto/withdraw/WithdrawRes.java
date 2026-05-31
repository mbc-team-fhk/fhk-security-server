package com.fhk.security.models.account.dto.withdraw;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class WithdrawRes {
    private Long id;
    private String nickname;
    private LocalDateTime withdrawDate;
}
