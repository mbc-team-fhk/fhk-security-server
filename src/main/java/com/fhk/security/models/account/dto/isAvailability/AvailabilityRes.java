package com.fhk.security.models.account.dto.isAvailability;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvailabilityRes {
    private boolean available;
}
