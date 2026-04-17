package com.proyecto_avanzada.controller;

import com.proyecto_avanzada.dto.GlobalDTOs;
import com.proyecto_avanzada.domain.enums.NivelPrioridad;
import com.proyecto_avanzada.dto.SolicitudDTOs;
import com.proyecto_avanzada.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public GlobalDTOs.SuccessResponse<SolicitudDTOs.SolicitudResponse> crear(
            @Valid @RequestBody SolicitudDTOs.SolicitudRequest request,
            org.springframework.security.core.Authentication authentication) {
        return new GlobalDTOs.SuccessResponse<>("Solicitud creada exitosamente",
                solicitudService.crearSolicitud(request, authentication.getName()));
    }

    @GetMapping
    public Page<SolicitudDTOs.SolicitudResponse> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long tipoId,
            @RequestParam(required = false) NivelPrioridad prioridad,
            @RequestParam(required = false) Long responsableId,
            Pageable pageable) {
        return solicitudService.listarSolicitudes(estado, tipoId, prioridad, responsableId, pageable);
    }

    @GetMapping("/{id}")
    public GlobalDTOs.SuccessResponse<SolicitudDTOs.SolicitudResponse> detalle(@PathVariable Long id) {
        return new GlobalDTOs.SuccessResponse<>("Detalle de solicitud", solicitudService.obtenerDetalle(id));
    }

    @PutMapping("/{id}/clasificacion")
    @PreAuthorize("hasRole('COORDINADOR')")
    public GlobalDTOs.SuccessResponse<SolicitudDTOs.SolicitudResponse> clasificar(
            @PathVariable Long id,
            @Valid @RequestBody SolicitudDTOs.ClasificacionRequest request,
            Authentication authentication) {
        return new GlobalDTOs.SuccessResponse<>("Solicitud clasificada",
                solicitudService.clasificarSolicitud(id, request, authentication.getName()));
    }

    @PostMapping("/{id}/asignacion")
    @PreAuthorize("hasRole('COORDINADOR')")
    public GlobalDTOs.SuccessResponse<Void> asignar(
            @PathVariable Long id,
            @Valid @RequestBody SolicitudDTOs.AsignacionRequest request,
            Authentication authentication) {
        solicitudService.asignarSolicitud(id, request, authentication.getName());
        return new GlobalDTOs.SuccessResponse<>("Solicitud asignada", null);
    }

    @PutMapping("/{id}/atencion")
    @PreAuthorize("hasRole('COORDINADOR')")
    public GlobalDTOs.SuccessResponse<Void> atender(
            @PathVariable Long id,
            @Valid @RequestBody SolicitudDTOs.AtencionRequest request,
            Authentication authentication) {
        solicitudService.atenderSolicitud(id, request, authentication.getName());
        return new GlobalDTOs.SuccessResponse<>("Solicitud atendida", null);
    }

    @GetMapping("/{id}/historial")
    public GlobalDTOs.SuccessResponse<List<SolicitudDTOs.HistorialResponse>> historial(@PathVariable Long id) {
        return new GlobalDTOs.SuccessResponse<>("Historial de cambios", solicitudService.obtenerHistorial(id));
    }

    @PostMapping("/{id}/cierre")
    @PreAuthorize("hasRole('COORDINADOR')")
    public GlobalDTOs.SuccessResponse<Void> cierre(
            @PathVariable Long id,
            @Valid @RequestBody SolicitudDTOs.CierreRequest request,
            Authentication authentication) {
        solicitudService.cerrarSolicitud(id, request, authentication.getName());
        return new GlobalDTOs.SuccessResponse<>("Solicitud cerrada", null);
    }

    @GetMapping("/{id}/resumen")
    @PreAuthorize("hasRole('COORDINADOR')")
    public GlobalDTOs.SuccessResponse<String> resumen(@PathVariable Long id) {
        return new GlobalDTOs.SuccessResponse<>("Resumen generado", solicitudService.generarResumen(id));
    }
}
