package com.proyecto_avanzada.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.proyecto_avanzada.domain.entity.TipoSolicitud;
import com.proyecto_avanzada.dto.CatalogoDTOs;

@Mapper(componentModel = "spring")
public interface TipoSolicitudMapper {

    CatalogoDTOs.TipoSolicitudResponse toResponse(TipoSolicitud tipo);

    @Mapping(target = "id", ignore = true)
    TipoSolicitud toEntity(CatalogoDTOs.TipoSolicitudRequest request);
}
