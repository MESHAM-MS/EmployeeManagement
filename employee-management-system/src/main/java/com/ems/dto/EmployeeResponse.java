package com.ems.dto;

import java.math.BigDecimal;

public record EmployeeResponse(
        Long id,
        String name,
        String email,
        BigDecimal salary,
        DepartmentResponse department,
        DesignationResponse designation,
        AddressResponse address) {
}
