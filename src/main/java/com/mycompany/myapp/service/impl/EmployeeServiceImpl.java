package com.mycompany.myapp.service.impl;

import com.mycompany.core.data.SecureDataManager;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.service.EmployeeService;
import com.mycompany.myapp.service.dto.EmployeeDTO;
import com.mycompany.myapp.service.mapper.EmployeeMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private static final String FETCH_PLAN_CODE = "employee-basic";

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final SecureDataManager secureDataManager;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper, SecureDataManager secureDataManager) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.secureDataManager = secureDataManager;
    }

    @Override
    public EmployeeDTO save(EmployeeDTO employeeDTO) {
        LOG.debug("Request to save Employee : {}", employeeDTO);
        Employee employee = employeeMapper.toEntity(employeeDTO);
        Employee saved = employeeRepository.save(employee);
        return toDto(secureDataManager.loadOne(Employee.class, saved.getId(), FETCH_PLAN_CODE));
    }

    @Override
    public EmployeeDTO update(EmployeeDTO employeeDTO) {
        LOG.debug("Request to update Employee : {}", employeeDTO);
        Map<String, Object> payload = new HashMap<>();
        payload.put("code", employeeDTO.getCode());
        payload.put("name", employeeDTO.getName());
        return toDto(secureDataManager.save(Employee.class, employeeDTO.getId(), payload, FETCH_PLAN_CODE));
    }

    @Override
    public Optional<EmployeeDTO> partialUpdate(EmployeeDTO employeeDTO) {
        LOG.debug("Request to partially update Employee : {}", employeeDTO);
        Map<String, Object> payload = new HashMap<>();
        if (employeeDTO.getCode() != null) {
            payload.put("code", employeeDTO.getCode());
        }
        if (employeeDTO.getName() != null) {
            payload.put("name", employeeDTO.getName());
        }
        return Optional.of(toDto(secureDataManager.save(Employee.class, employeeDTO.getId(), payload, FETCH_PLAN_CODE)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Employees");
        return secureDataManager.loadPage(Employee.class, null, pageable, FETCH_PLAN_CODE).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeDTO> findOne(Long id) {
        LOG.debug("Request to get Employee : {}", id);
        try {
            return Optional.of(toDto(secureDataManager.loadOne(Employee.class, id, FETCH_PLAN_CODE)));
        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Employee : {}", id);
        secureDataManager.delete(Employee.class, id);
    }

    private EmployeeDTO toDto(Map<String, Object> values) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(asLong(values.get("id")));
        dto.setCode((String) values.get("code"));
        dto.setName((String) values.get("name"));
        return dto;
    }

    private Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long l) {
            return l;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        return Long.valueOf(value.toString());
    }
}
