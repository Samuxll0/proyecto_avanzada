package com.proyecto_avanzada.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.proyecto_avanzada.domain.entity.Solicitud;
import com.proyecto_avanzada.dto.SolicitudDTOs;

@Mapper(componentModel = "spring")
public interface SolicitudMapper {

    @Mapping(source = "tipoSolicitud.id", target = "tipoSolicitudId")
    @Mapping(source = "usuarioAsignado.id", target = "usuarioAsignadoId")
    @Mapping(source = "usuarioAsignado.nombre", target = "usuarioAsignadoNombre")
    @Mapping(source = "solicitante.id", target = "solicitanteId")
    @Mapping(source = "solicitante.nombre", target = "solicitanteNombre")
    @Mapping(source = "solicitante.email", target = "solicitanteEmail")
    SolicitudDTOs.SolicitudResponse toResponse(Solicitud solicitud);

}
