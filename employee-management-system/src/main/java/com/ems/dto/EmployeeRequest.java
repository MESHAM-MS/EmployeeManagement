package com.ems.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/** Used for POST (create) and PUT (full update). */
public record EmployeeRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        @Size(max = 150)
        String email,

        @NotNull(message = "Salary is required")
        @Positive(message = "Salary must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Salary must have at most 10 integer and 2 decimal digits")
        BigDecimal salary,

        @NotNull(message = "Department id is required") Long departmentId,

        @NotNull(message = "Designation id is required") Long designationId,

        @NotNull(message = "Address is required") @Valid AddressRequest address) {
}
