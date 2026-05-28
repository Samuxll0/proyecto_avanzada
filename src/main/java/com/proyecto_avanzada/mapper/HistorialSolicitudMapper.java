package com.proyecto_avanzada.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.proyecto_avanzada.domain.entity.HistorialSolicitud;
import com.proyecto_avanzada.dto.SolicitudDTOs;

@Mapper(componentModel = "spring")
public interface HistorialSolicitudMapper {

    @Mapping(source = "autorCambio.nombre", target = "autorCambioNombre")
    SolicitudDTOs.HistorialResponse toResponse(HistorialSolicitud historial);

}
