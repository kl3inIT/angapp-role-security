package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.EmployeeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    EmployeeDTO save(EmployeeDTO employeeDTO);

    EmployeeDTO update(EmployeeDTO employeeDTO);

    Optional<EmployeeDTO> partialUpdate(EmployeeDTO employeeDTO);

    Page<EmployeeDTO> findAll(Pageable pageable);

    Optional<EmployeeDTO> findOne(Long id);

    void delete(Long id);
}
