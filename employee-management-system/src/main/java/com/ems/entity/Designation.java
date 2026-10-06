package com.ems.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "designations")
public class Designation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "designation_id")
    private Long id;

    @Column(name = "designation_name", nullable = false, unique = true, length = 100)
    private String name;

    // One Designation -> Many Employees
    @OneToMany(mappedBy = "designation")
    private List<Employee> employees = new ArrayList<>();

    public Designation() {
    }

    public Designation(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
}
