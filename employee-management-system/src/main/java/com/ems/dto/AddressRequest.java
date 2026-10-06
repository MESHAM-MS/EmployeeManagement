package com.ems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "City is required") @Size(max = 100) String city,
        @NotBlank(message = "State is required") @Size(max = 100) String state,
        @NotBlank(message = "Country is required") @Size(max = 100) String country) {
}
