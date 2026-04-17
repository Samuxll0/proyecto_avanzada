package com.proyecto_avanzada.dto;

import com.proyecto_avanzada.domain.enums.NivelPrioridad;
import jakarta.validation.constraints.NotBlank;

public class IADTOs {

        public record IARequest(
                        @NotBlank String descripcion) {
        }

        public record IAResponse(
                        Long tipoSugeridoId,
                        String tipoSugeridoNombre,
                        NivelPrioridad prioridadSugerida,
                        Double confianza) {
        }

        public record ResumenResponse(
                        String resumenTextual) {
        }
}
