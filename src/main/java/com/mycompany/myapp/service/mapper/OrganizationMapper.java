package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Organization;
import com.mycompany.myapp.service.dto.OrganizationDTO;
import org.mapstruct.*;
import org.mapstruct.BeanMapping;

/**
 * Mapper for the entity {@link Organization} and its DTO {@link OrganizationDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrganizationMapper extends EntityMapper<OrganizationDTO, Organization> {
    @Override
    @Mapping(target = "emplList", ignore = true)
    Organization toEntity(OrganizationDTO dto);

    @Override
    @Mapping(target = "employeeIds", ignore = true)
    @Mapping(target = "emplList", ignore = true)
    OrganizationDTO toDto(Organization entity);

    @Override
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    void partialUpdate(@MappingTarget Organization entity, OrganizationDTO dto);
}
