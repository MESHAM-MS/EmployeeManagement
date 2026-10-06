package com.ems.service;

import com.ems.dto.DesignationRequest;
import com.ems.dto.DesignationResponse;
import com.ems.entity.Designation;
import com.ems.exception.DuplicateResourceException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.mapper.EmployeeMapper;
import com.ems.repository.DesignationRepository;
import com.ems.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DesignationService {

    private final DesignationRepository designationRepository;
    private final EmployeeRepository employeeRepository;

    public DesignationService(DesignationRepository designationRepository, EmployeeRepository employeeRepository) {
        this.designationRepository = designationRepository;
        this.employeeRepository = employeeRepository;
    }

    public DesignationResponse create(DesignationRequest request) {
        String name = request.name().trim();
        if (designationRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Designation '" + name + "' already exists");
        }
        return EmployeeMapper.toResponse(designationRepository.save(new Designation(name)));
    }

    @Transactional(readOnly = true)
    public List<DesignationResponse> getAll() {
        return designationRepository.findAll().stream().map(EmployeeMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DesignationResponse getById(Long id) {
        return EmployeeMapper.toResponse(find(id));
    }

    public DesignationResponse update(Long id, DesignationRequest request) {
        Designation designation = find(id);
        String name = request.name().trim();
        if (designationRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateResourceException("Designation '" + name + "' already exists");
        }
        designation.setName(name);
        return EmployeeMapper.toResponse(designation);
    }

    public void delete(Long id) {
        Designation designation = find(id);
        if (employeeRepository.existsByDesignationId(id)) {
            throw new DuplicateResourceException(
                    "Cannot delete designation '" + designation.getName() + "' because employees are assigned to it");
        }
        designationRepository.delete(designation);
    }

    private Designation find(Long id) {
        return designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation", id));
    }
}
