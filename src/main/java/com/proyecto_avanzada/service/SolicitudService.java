package com.proyecto_avanzada.service;

import com.proyecto_avanzada.domain.entity.*;
import com.proyecto_avanzada.domain.enums.EstadoSolicitud;
import com.proyecto_avanzada.dto.SolicitudDTOs;
import com.proyecto_avanzada.repository.*;
import com.proyecto_avanzada.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.proyecto_avanzada.domain.enums.NivelPrioridad;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoSolicitudRepository tipoSolicitudRepository;
    private final AsignacionRepository asignacionRepository;
    private final HistorialSolicitudRepository historialRepository;
    private final AIService aiService;

    @Transactional
    public SolicitudDTOs.SolicitudResponse crearSolicitud(SolicitudDTOs.SolicitudRequest request, String emailAutor) {
        Usuario autor = obtenerAutor(emailAutor);
        if (autor == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado de forma válida.");
        }

        Solicitud solicitud = Solicitud.builder()
                .descripcion(request.descripcion())
                .canalOrigen(request.canalOrigen())
                .solicitante(autor)
                .estado(EstadoSolicitud.REGISTRADA)
                .build();

        Solicitud saved = solicitudRepository.save(solicitud);
        registrarHistorial(saved, null, EstadoSolicitud.REGISTRADA, "Creación de solicitud", autor);

        return mapToResponse(saved);
    }

    public Page<SolicitudDTOs.SolicitudResponse> listarSolicitudes(String estado, Long tipoId,
            NivelPrioridad prioridad, Long responsableId, Pageable pageable) {
        EstadoSolicitud estadoEnum = null;
        if (estado != null && !estado.isEmpty()) {
            try {
                estadoEnum = EstadoSolicitud.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore or handle
            }
        }
        return solicitudRepository.findByFiltros(estadoEnum, tipoId, prioridad, responsableId, pageable)
                .map(this::mapToResponse);
    }

    public SolicitudDTOs.SolicitudResponse obtenerDetalle(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));
        return mapToResponse(solicitud);
    }

    @Transactional
    public SolicitudDTOs.SolicitudResponse clasificarSolicitud(Long id, SolicitudDTOs.ClasificacionRequest request,
            String emailAutor) {
        Solicitud solicitud = obtenerSolicitudModificable(id);

        if (solicitud.getEstado() != EstadoSolicitud.REGISTRADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se pueden clasificar solicitudes en estado REGISTRADA.");
        }

        TipoSolicitud tipo = tipoSolicitudRepository.findById(request.tipoSolicitudId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de Solicitud no encontrado"));

        EstadoSolicitud estadoAnterior = solicitud.getEstado();
        Usuario autor = obtenerAutor(emailAutor);

        solicitud.setTipoSolicitud(tipo);
        solicitud.setPrioridad(request.prioridad());
        solicitud.setJustificacionPrioridad(request.justificacionPrioridad());
        solicitud.setEstado(EstadoSolicitud.CLASIFICADA);

        Solicitud saved = solicitudRepository.save(solicitud);
        registrarHistorial(saved, estadoAnterior, EstadoSolicitud.CLASIFICADA, "Solicitud clasificada y priorizada.",
                autor);

        return mapToResponse(saved);
    }

    @Transactional
    public void asignarSolicitud(Long id, SolicitudDTOs.AsignacionRequest request, String emailAutor) {
        Solicitud solicitud = obtenerSolicitudModificable(id);

        if (solicitud.getEstado() != EstadoSolicitud.CLASIFICADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se pueden asignar solicitudes en estado CLASIFICADA.");
        }

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (!usuario.isActivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario asignado de destino no está activo.");
        }

        EstadoSolicitud estadoAnterior = solicitud.getEstado();
        Usuario autor = obtenerAutor(emailAutor);

        solicitud.setUsuarioAsignado(usuario);
        solicitud.setEstado(EstadoSolicitud.EN_ATENCION);
        solicitudRepository.save(solicitud);

        Asignacion asignacion = Asignacion.builder()
                .solicitud(solicitud)
                .usuario(usuario)
                .build();
        asignacionRepository.save(asignacion);

        registrarHistorial(solicitud, estadoAnterior, EstadoSolicitud.EN_ATENCION,
                "Solicitud asignada a " + usuario.getEmail(), autor);
    }

    @Transactional
    public void atenderSolicitud(Long id, SolicitudDTOs.AtencionRequest request, String emailAutor) {
        Solicitud solicitud = obtenerSolicitudModificable(id);

        if (solicitud.getEstado() != EstadoSolicitud.EN_ATENCION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se pueden marcar como atendidas solicitudes en estado EN_ATENCION.");
        }

        EstadoSolicitud estadoAnterior = solicitud.getEstado();
        Usuario autor = obtenerAutor(emailAutor);

        solicitud.setEstado(EstadoSolicitud.ATENDIDA);
        solicitudRepository.save(solicitud);

        registrarHistorial(solicitud, estadoAnterior, EstadoSolicitud.ATENDIDA, request.comentariosAtencion(), autor);
    }

    @Transactional
    public void cerrarSolicitud(Long id, SolicitudDTOs.CierreRequest request, String emailAutor) {
        Solicitud solicitud = obtenerSolicitudModificable(id);

        if (solicitud.getEstado() != EstadoSolicitud.ATENDIDA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La solicitud solo puede cerrarse si previamente ha sido ATENDIDA.");
        }

        EstadoSolicitud estadoAnterior = solicitud.getEstado();
        Usuario autor = obtenerAutor(emailAutor);

        solicitud.setEstado(EstadoSolicitud.CERRADA);
        solicitudRepository.save(solicitud);

        registrarHistorial(solicitud, estadoAnterior, EstadoSolicitud.CERRADA, request.comentariosCierre(), autor);
    }

    private Solicitud obtenerSolicitudModificable(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));
        if (solicitud.getEstado() == EstadoSolicitud.CERRADA) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "La solicitud está CERRADA y no puede ser modificada.");
        }
        return solicitud;
    }

    private Usuario obtenerAutor(String email) {
        if (email == null)
            return null;
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public List<SolicitudDTOs.HistorialResponse> obtenerHistorial(Long id) {
        List<HistorialSolicitud> historiales = historialRepository.findBySolicitudIdOrderByFechaCambioDesc(id);
        return historiales.stream()
                .map(h -> new SolicitudDTOs.HistorialResponse(
                        h.getId(), h.getEstadoAnterior(), h.getEstadoNuevo(),
                        h.getFechaCambio(), h.getComentarios()))
                .collect(Collectors.toList());
    }

    private void registrarHistorial(Solicitud solicitud, EstadoSolicitud estadoAnterior, EstadoSolicitud estadoNuevo,
            String comentarios, Usuario autor) {
        HistorialSolicitud historial = HistorialSolicitud.builder()
                .solicitud(solicitud)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .comentarios(comentarios)
                .autorCambio(autor)
                .build();
        historialRepository.save(historial);
    }

    private SolicitudDTOs.SolicitudResponse mapToResponse(Solicitud s) {
        return new SolicitudDTOs.SolicitudResponse(
                s.getId(),
                s.getDescripcion(),
                s.getEstado(),
                s.getCanalOrigen(),
                s.getTipoSolicitud() != null ? s.getTipoSolicitud().getId() : null,
                s.getPrioridad(),
                s.getJustificacionPrioridad(),
                s.getUsuarioAsignado() != null ? s.getUsuarioAsignado().getId() : null,
                s.getSolicitante() != null ? s.getSolicitante().getId() : null,
                s.getFechaCreacion());
    }

    public String generarResumen(Long id) {
        List<HistorialSolicitud> historiales = historialRepository.findBySolicitudIdOrderByFechaCambioDesc(id);
        return aiService.generarResumenHistorial(historiales).resumenTextual();
    }
}
