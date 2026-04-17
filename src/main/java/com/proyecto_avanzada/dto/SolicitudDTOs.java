package com.proyecto_avanzada.dto;

import com.proyecto_avanzada.domain.enums.NivelPrioridad;
import com.proyecto_avanzada.domain.enums.CanalOrigen;
import com.proyecto_avanzada.domain.enums.EstadoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
                        @NotNull NivelPrioridad prioridad,
                        @NotBlank String justificacionPrioridad) {
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
