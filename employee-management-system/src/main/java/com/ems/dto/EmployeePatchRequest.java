package com.ems.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/** Used for PATCH: every field is optional, but any value that is supplied is still validated. */
public record EmployeePatchRequest(
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        @Pattern(regexp = ".*\\S.*", message = "Name must not be blank")
        String name,

        @Email(message = "Email must be a valid email address")
        @Size(max = 150)
        String email,

        @Positive(message = "Salary must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Salary must have at most 10 integer and 2 decimal digits")
        BigDecimal salary,

        Long departmentId,

        Long designationId,

        @Valid AddressPatchRequest address) {
}
