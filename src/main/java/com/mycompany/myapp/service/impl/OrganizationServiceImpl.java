package com.mycompany.myapp.service.impl;

import com.mycompany.core.data.SecureDataManager;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.OrgEmpl;
import com.mycompany.myapp.domain.Organization;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.OrgEmplRepository;
import com.mycompany.myapp.repository.OrganizationRepository;
import com.mycompany.myapp.service.OrganizationService;
import com.mycompany.myapp.service.dto.EmployeeDTO;
import com.mycompany.myapp.service.dto.OrganizationDTO;
import com.mycompany.myapp.service.dto.OrganizationEmployeeLinkDTO;
import com.mycompany.myapp.service.mapper.OrganizationMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Organization}.
 */
@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private static final String DETAIL_FETCH_PLAN_CODE = "organization-detail";

    private static final String LIST_FETCH_PLAN_CODE = DETAIL_FETCH_PLAN_CODE;

    private final OrganizationRepository organizationRepository;

    private final OrganizationMapper organizationMapper;

    private final EmployeeRepository employeeRepository;

    private final OrgEmplRepository orgEmplRepository;

    private final SecureDataManager secureDataManager;

    public OrganizationServiceImpl(
        OrganizationRepository organizationRepository,
        OrganizationMapper organizationMapper,
        EmployeeRepository employeeRepository,
        OrgEmplRepository orgEmplRepository,
        SecureDataManager secureDataManager
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
        this.employeeRepository = employeeRepository;
        this.orgEmplRepository = orgEmplRepository;
        this.secureDataManager = secureDataManager;
    }

    @Override
    public OrganizationDTO save(OrganizationDTO organizationDTO) {
        LOG.debug("Request to save Organization : {}", organizationDTO);
        Organization organization = organizationMapper.toEntity(organizationDTO);
        syncEmployees(organization, organizationDTO.getEmployeeIds());
        Organization saved = organizationRepository.save(organization);

        // Enforce secure attribute view and fetch-plan on the response.
        Map<String, Object> values = secureDataManager.loadOne(Organization.class, saved.getId(), DETAIL_FETCH_PLAN_CODE);
        return toDto(values);
    }

    @Override
    public OrganizationDTO update(OrganizationDTO organizationDTO) {
        LOG.debug("Request to update Organization : {}", organizationDTO);
        Map<String, Object> payload = new HashMap<>();
        payload.put("code", organizationDTO.getCode());
        payload.put("name", organizationDTO.getName());
        payload.put("description", organizationDTO.getDescription());

        secureDataManager.save(Organization.class, organizationDTO.getId(), payload, DETAIL_FETCH_PLAN_CODE);
        if (organizationDTO.getEmployeeIds() != null) {
            Organization organization = organizationRepository
                .findById(organizationDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
            syncEmployees(organization, organizationDTO.getEmployeeIds());
            organizationRepository.save(organization);
        }

        Map<String, Object> values = secureDataManager.loadOne(Organization.class, organizationDTO.getId(), DETAIL_FETCH_PLAN_CODE);
        return toDto(values);
    }

    @Override
    public Optional<OrganizationDTO> partialUpdate(OrganizationDTO organizationDTO) {
        LOG.debug("Request to partially update Organization : {}", organizationDTO);
        Map<String, Object> payload = new HashMap<>();
        if (organizationDTO.getCode() != null) {
            payload.put("code", organizationDTO.getCode());
        }
        if (organizationDTO.getName() != null) {
            payload.put("name", organizationDTO.getName());
        }
        if (organizationDTO.getDescription() != null) {
            payload.put("description", organizationDTO.getDescription());
        }

        if (!payload.isEmpty()) {
            secureDataManager.save(Organization.class, organizationDTO.getId(), payload, DETAIL_FETCH_PLAN_CODE);
        }

        if (organizationDTO.getEmployeeIds() != null) {
            Organization organization = organizationRepository
                .findById(organizationDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
            syncEmployees(organization, organizationDTO.getEmployeeIds());
            organizationRepository.save(organization);
        }

        Map<String, Object> values = secureDataManager.loadOne(Organization.class, organizationDTO.getId(), DETAIL_FETCH_PLAN_CODE);
        return Optional.of(toDto(values));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrganizationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Organizations");
        return secureDataManager.loadPage(Organization.class, null, pageable, LIST_FETCH_PLAN_CODE).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizationDTO> findOne(Long id) {
        LOG.debug("Request to get Organization : {}", id);
        try {
            Map<String, Object> values = secureDataManager.loadOne(Organization.class, id, DETAIL_FETCH_PLAN_CODE);
            return Optional.of(toDto(values));
        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Organization : {}", id);
        secureDataManager.delete(Organization.class, id);
    }

    private OrganizationDTO toDto(Map<String, Object> values) {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(asLong(values.get("id")));
        dto.setCode((String) values.get("code"));
        dto.setName((String) values.get("name"));
        if (values.containsKey("description")) {
            dto.setDescription((String) values.get("description"));
        }
        Object rawEmployees = values.get("emplList");
        if (rawEmployees instanceof List<?> employees) {
            List<OrganizationEmployeeLinkDTO> employeeLinks = employees
                .stream()
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(this::toEmployeeLinkDto)
                .filter(Objects::nonNull)
                .toList();
            dto.setEmplList(employeeLinks);
        }
        return dto;
    }

    private OrganizationEmployeeLinkDTO toEmployeeLinkDto(Map<?, ?> values) {
        OrganizationEmployeeLinkDTO dto = new OrganizationEmployeeLinkDTO();
        dto.setId(asLong(values.get("id")));
        dto.setManager((String) values.get("manager"));
        dto.setImportDate((String) values.get("importDate"));

        Object employee = values.get("employee");
        if (!(employee instanceof Map<?, ?> employeeValues)) {
            return null;
        }
        dto.setEmployee(toEmployeeDto(employeeValues));
        return dto;
    }

    private EmployeeDTO toEmployeeDto(Map<?, ?> values) {
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

    private void syncEmployees(Organization organization, List<Long> employeeIds) {
        List<Long> distinctIds = employeeIds == null ? List.of() : employeeIds.stream().distinct().toList();
        List<Employee> employees = employeeRepository.findAllById(distinctIds);
        if (employees.size() != distinctIds.size()) {
            throw new EntityNotFoundException("One or more employees not found");
        }

        if (!organization.getEmplList().isEmpty()) {
            orgEmplRepository.deleteAll(new ArrayList<>(organization.getEmplList()));
            organization.getEmplList().clear();
        }

        for (Employee employee : employees) {
            OrgEmpl link = new OrgEmpl();
            link.setOrganization(organization);
            link.setEmployee(employee);
            organization.getEmplList().add(link);
        }
    }
}
