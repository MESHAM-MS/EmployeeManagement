package com.ems.controller;

import com.ems.dto.DesignationRequest;
import com.ems.dto.DesignationResponse;
import com.ems.service.DesignationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/designations")
public class DesignationController {

    private final DesignationService designationService;

    public DesignationController(DesignationService designationService) {
        this.designationService = designationService;
    }

    @PostMapping
    public ResponseEntity<DesignationResponse> create(@Valid @RequestBody DesignationRequest request) {
        DesignationResponse created = designationService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<DesignationResponse> getAll() {
        return designationService.getAll();
    }

    @GetMapping("/{id}")
    public DesignationResponse getById(@PathVariable Long id) {
        return designationService.getById(id);
    }

    @PutMapping("/{id}")
    public DesignationResponse update(@PathVariable Long id, @Valid @RequestBody DesignationRequest request) {
        return designationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        designationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
