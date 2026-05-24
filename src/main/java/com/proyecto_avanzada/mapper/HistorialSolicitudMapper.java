package com.proyecto_avanzada.mapper;

import org.mapstruct.Mapper;

import com.proyecto_avanzada.domain.entity.HistorialSolicitud;
import com.proyecto_avanzada.dto.SolicitudDTOs;

@Mapper(componentModel = "spring")
public interface HistorialSolicitudMapper {

    SolicitudDTOs.HistorialResponse toResponse(HistorialSolicitud historial);

}
