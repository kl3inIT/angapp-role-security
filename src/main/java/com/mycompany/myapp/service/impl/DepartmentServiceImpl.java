package com.mycompany.myapp.service.impl;

import com.mycompany.core.data.SecureDataManager;
import com.mycompany.myapp.domain.Department;
import com.mycompany.myapp.repository.DepartmentRepository;
import com.mycompany.myapp.service.DepartmentService;
import com.mycompany.myapp.service.dto.DepartmentDTO;
import com.mycompany.myapp.service.mapper.DepartmentMapper;
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

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Department}.
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger LOG = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private static final String FETCH_PLAN_CODE = "department-basic";

    private final DepartmentRepository departmentRepository;

    private final DepartmentMapper departmentMapper;

    private final SecureDataManager secureDataManager;

    public DepartmentServiceImpl(
        DepartmentRepository departmentRepository,
        DepartmentMapper departmentMapper,
        SecureDataManager secureDataManager
    ) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
        this.secureDataManager = secureDataManager;
    }

    @Override
    public DepartmentDTO save(DepartmentDTO departmentDTO) {
        LOG.debug("Request to save Department : {}", departmentDTO);
        Department department = departmentMapper.toEntity(departmentDTO);
        Department saved = departmentRepository.save(department);

        // Enforce secure attribute view and fetch-plan on the response.
        Map<String, Object> values = secureDataManager.loadOne(Department.class, saved.getId(), FETCH_PLAN_CODE);
        return toDto(values);
    }

    @Override
    public DepartmentDTO update(DepartmentDTO departmentDTO) {
        LOG.debug("Request to update Department : {}", departmentDTO);
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", departmentDTO.getName());
        payload.put("code", departmentDTO.getCode());

        Map<String, Object> values = secureDataManager.save(Department.class, departmentDTO.getId(), payload, FETCH_PLAN_CODE);
        return toDto(values);
    }

    @Override
    public Optional<DepartmentDTO> partialUpdate(DepartmentDTO departmentDTO) {
        LOG.debug("Request to partially update Department : {}", departmentDTO);
        Map<String, Object> payload = new HashMap<>();
        if (departmentDTO.getName() != null) {
            payload.put("name", departmentDTO.getName());
        }
        if (departmentDTO.getCode() != null) {
            payload.put("code", departmentDTO.getCode());
        }

        Map<String, Object> values = secureDataManager.save(Department.class, departmentDTO.getId(), payload, FETCH_PLAN_CODE);
        return Optional.of(toDto(values));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Departments");
        return secureDataManager.loadPage(Department.class, null, pageable, FETCH_PLAN_CODE).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DepartmentDTO> findOne(Long id) {
        LOG.debug("Request to get Department : {}", id);
        try {
            Map<String, Object> values = secureDataManager.loadOne(Department.class, id, FETCH_PLAN_CODE);
            return Optional.of(toDto(values));
        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Department : {}", id);
        secureDataManager.delete(Department.class, id);
    }

    private DepartmentDTO toDto(Map<String, Object> values) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(asLong(values.get("id")));
        dto.setName((String) values.get("name"));
        dto.setCode((String) values.get("code"));
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
