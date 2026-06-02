package com.chambercript_for_lawyers.backend.repository;

import com.chambercript_for_lawyers.backend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByAdminId(Long adminId);
}
