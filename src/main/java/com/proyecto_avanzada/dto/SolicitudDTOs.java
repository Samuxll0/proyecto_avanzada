package com.proyecto_avanzada.dto;

import java.time.LocalDateTime;

import com.proyecto_avanzada.domain.enums.CanalOrigen;
import com.proyecto_avanzada.domain.enums.EstadoSolicitud;
import com.proyecto_avanzada.domain.enums.NivelPrioridad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SolicitudDTOs {

        public record SolicitudRequest(
                        @NotBlank String descripcion,
                        @NotNull CanalOrigen canalOrigen) {
        }

        public record SolicitudResponse(
                        Long id,
                        String descripcion,
                        EstadoSolicitud estado,
                        CanalOrigen canalOrigen,
                        Long tipoSolicitudId,
                        NivelPrioridad prioridad,
                        String justificacionPrioridad,
                        Long usuarioAsignadoId,
                        Long solicitanteId,
                        LocalDateTime fechaCreacion) {
        }

        public record ClasificacionRequest(
                        @NotNull Long tipoSolicitudId,
                        NivelPrioridad prioridad,
                        String justificacionPrioridad,
                        Boolean impactoAcademico,
                        LocalDateTime fechaLimite) {

                        public ClasificacionRequest(Long tipoSolicitudId, NivelPrioridad prioridad, String justificacionPrioridad) {
                                this(tipoSolicitudId, prioridad, justificacionPrioridad, null, null);
                        }

        }

        public record AtencionRequest(
                        @NotBlank String comentariosAtencion) {
        }

        public record AsignacionRequest(
                        @NotNull Long usuarioId) {
        }

        public record CierreRequest(
                        @NotBlank String comentariosCierre) {
        }

        public record HistorialResponse(
                        Long id,
                        EstadoSolicitud estadoAnterior,
                        EstadoSolicitud estadoNuevo,
                        LocalDateTime fechaCambio,
                        String comentarios) {
        }
}
