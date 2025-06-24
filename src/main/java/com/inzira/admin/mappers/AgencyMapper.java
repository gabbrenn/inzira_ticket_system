package com.inzira.admin.mappers;

import com.inzira.agency.models.Agency;
import com.inzira.admin.dtos.*;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgencyMapper {

    AgencyDTO toDTO(Agency agency);

    Agency toEntity(AgencyRegistrationDTO dto);

    void updateEntityFromDTO(AgencyUpdateDTO dto, @MappingTarget Agency agency);
}
