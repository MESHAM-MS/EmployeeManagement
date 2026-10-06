package com.ems.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** All fields optional: only the ones supplied are changed. */
public record AddressPatchRequest(
        @Size(max = 100) @Pattern(regexp = ".*\\S.*", message = "City must not be blank") String city,
        @Size(max = 100) @Pattern(regexp = ".*\\S.*", message = "State must not be blank") String state,
        @Size(max = 100) @Pattern(regexp = ".*\\S.*", message = "Country must not be blank") String country) {
}
