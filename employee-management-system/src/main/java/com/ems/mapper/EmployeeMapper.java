package com.ems.mapper;

import com.ems.dto.*;
import com.ems.entity.Department;
import com.ems.entity.Designation;
import com.ems.entity.Employee;
import com.ems.entity.EmployeeAddress;

public final class EmployeeMapper {

    private EmployeeMapper() {
    }

    public static DepartmentResponse toResponse(Department d) {
        return new DepartmentResponse(d.getId(), d.getName());
    }

    public static DesignationResponse toResponse(Designation d) {
        return new DesignationResponse(d.getId(), d.getName());
    }

    public static AddressResponse toResponse(EmployeeAddress a) {
        return a == null ? null : new AddressResponse(a.getId(), a.getCity(), a.getState(), a.getCountry());
    }

    public static EmployeeResponse toResponse(Employee e) {
        return new EmployeeResponse(
                e.getId(),
                e.getName(),
                e.getEmail(),
                e.getSalary(),
                toResponse(e.getDepartment()),
                toResponse(e.getDesignation()),
                toResponse(e.getAddress()));
    }
}
