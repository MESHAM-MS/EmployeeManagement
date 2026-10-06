package com.ems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DesignationRequest(
        @NotBlank(message = "Designation name is required")
        @Size(min = 2, max = 100, message = "Designation name must be between 2 and 100 characters")
        String name) {
}
