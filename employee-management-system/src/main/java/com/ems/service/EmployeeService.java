package com.ems.service;

import com.ems.dto.*;
import com.ems.entity.Department;
import com.ems.entity.Designation;
import com.ems.entity.Employee;
import com.ems.entity.EmployeeAddress;
import com.ems.exception.DuplicateResourceException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.mapper.EmployeeMapper;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.DesignationRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.EmployeeSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           DesignationRepository designationRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.designationRepository = designationRepository;
    }

    // ---------- CREATE ----------
    public EmployeeResponse create(EmployeeRequest request) {
        String email = request.email().trim();
        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Employee with email '" + email + "' already exists");
        }

        Employee employee = new Employee();
        employee.setName(request.name().trim());
        employee.setEmail(email);
        employee.setSalary(request.salary());
        employee.setDepartment(findDepartment(request.departmentId()));
        employee.setDesignation(findDesignation(request.designationId()));
        employee.setAddress(new EmployeeAddress(
                request.address().city().trim(),
                request.address().state().trim(),
                request.address().country().trim()));

        return EmployeeMapper.toResponse(employeeRepository.save(employee));
    }

    // ---------- READ ----------
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {
        return employeeRepository.findAll().stream().map(EmployeeMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return EmployeeMapper.toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> search(String name, Long departmentId, String departmentName) {
        return employeeRepository.findAll(EmployeeSpecifications.search(name, departmentId, departmentName))
                .stream().map(EmployeeMapper::toResponse).toList();
    }

    // ---------- UPDATE (PUT: full replace) ----------
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = find(id);
        String email = request.email().trim();
        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateResourceException("Employee with email '" + email + "' already exists");
        }

        employee.setName(request.name().trim());
        employee.setEmail(email);
        employee.setSalary(request.salary());
        employee.setDepartment(findDepartment(request.departmentId()));
        employee.setDesignation(findDesignation(request.designationId()));

        EmployeeAddress address = employee.getAddress();
        if (address == null) {
            employee.setAddress(new EmployeeAddress());
            address = employee.getAddress();
        }
        address.setCity(request.address().city().trim());
        address.setState(request.address().state().trim());
        address.setCountry(request.address().country().trim());

        return EmployeeMapper.toResponse(employee);
    }

    // ---------- UPDATE (PATCH: partial) ----------
    public EmployeeResponse patch(Long id, EmployeePatchRequest request) {
        Employee employee = find(id);

        if (request.name() != null) {
            employee.setName(request.name().trim());
        }
        if (request.email() != null) {
            String email = request.email().trim();
            if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
                throw new DuplicateResourceException("Employee with email '" + email + "' already exists");
            }
            employee.setEmail(email);
        }
        if (request.salary() != null) {
            employee.setSalary(request.salary());
        }
        if (request.departmentId() != null) {
            employee.setDepartment(findDepartment(request.departmentId()));
        }
        if (request.designationId() != null) {
            employee.setDesignation(findDesignation(request.designationId()));
        }
        if (request.address() != null) {
            patchAddress(employee, request.address());
        }

        return EmployeeMapper.toResponse(employee);
    }

    // ---------- DELETE ----------
    public void delete(Long id) {
        employeeRepository.delete(find(id)); // address is removed too (cascade + orphanRemoval)
    }

    // ---------- helpers ----------
    private void patchAddress(Employee employee, AddressPatchRequest patch) {
        EmployeeAddress address = employee.getAddress();
        if (address == null) {
            // No address yet: a new one needs all three parts.
            if (patch.city() == null || patch.state() == null || patch.country() == null) {
                throw new IllegalArgumentException("city, state and country are all required to add an address");
            }
            employee.setAddress(new EmployeeAddress(patch.city().trim(), patch.state().trim(),
                    patch.country().trim()));
            return;
        }
        if (patch.city() != null) address.setCity(patch.city().trim());
        if (patch.state() != null) address.setState(patch.state().trim());
        if (patch.country() != null) address.setCountry(patch.country().trim());
    }

    private Employee find(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }

    private Designation findDesignation(Long id) {
        return designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation", id));
    }
}
