package com.mycompany.myapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mycompany.core.data.SecureDataManager;
import com.mycompany.myapp.domain.Organization;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.OrgEmplRepository;
import com.mycompany.myapp.repository.OrganizationRepository;
import com.mycompany.myapp.service.dto.OrganizationDTO;
import com.mycompany.myapp.service.dto.OrganizationEmployeeLinkDTO;
import com.mycompany.myapp.service.mapper.OrganizationMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OrganizationServiceImplTest {

    @Test
    void shouldPreserveNestedEmployeeLinksFromFetchPlan() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        OrganizationMapper organizationMapper = mock(OrganizationMapper.class);
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        OrgEmplRepository orgEmplRepository = mock(OrgEmplRepository.class);
        SecureDataManager secureDataManager = mock(SecureDataManager.class);

        OrganizationServiceImpl service = new OrganizationServiceImpl(
            organizationRepository,
            organizationMapper,
            employeeRepository,
            orgEmplRepository,
            secureDataManager
        );

        when(secureDataManager.loadOne(Organization.class, 11L, "organization-detail")).thenReturn(
            Map.of(
                "id",
                11L,
                "code",
                "ORG-01",
                "name",
                "Org A",
                "emplList",
                List.of(
                    Map.of(
                        "id",
                        21L,
                        "manager",
                        "Y",
                        "importDate",
                        "2026-03-20",
                        "employee",
                        Map.of("id", 31L, "code", "EMP-01", "name", "Alice")
                    )
                )
            )
        );

        OrganizationDTO dto = service.findOne(11L).orElseThrow();

        assertThat(dto.getEmplList())
            .singleElement()
            .satisfies(link -> assertOrganizationEmployeeLink(link, 21L, "Y", "2026-03-20", 31L, "EMP-01", "Alice"));
    }

    private void assertOrganizationEmployeeLink(
        OrganizationEmployeeLinkDTO link,
        Long id,
        String manager,
        String importDate,
        Long employeeId,
        String employeeCode,
        String employeeName
    ) {
        assertThat(link.getId()).isEqualTo(id);
        assertThat(link.getManager()).isEqualTo(manager);
        assertThat(link.getImportDate()).isEqualTo(importDate);
        assertThat(link.getEmployee()).isNotNull();
        assertThat(link.getEmployee().getId()).isEqualTo(employeeId);
        assertThat(link.getEmployee().getCode()).isEqualTo(employeeCode);
        assertThat(link.getEmployee().getName()).isEqualTo(employeeName);
    }
}
