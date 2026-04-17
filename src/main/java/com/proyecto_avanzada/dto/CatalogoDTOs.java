package com.proyecto_avanzada.dto;

import com.proyecto_avanzada.domain.enums.NivelPrioridad;
import com.proyecto_avanzada.domain.enums.Rol;
import jakarta.validation.constraints.NotBlank;

public class CatalogoDTOs {

        public record UsuarioResponse(
                        Long id,
                        String nombre,
                        String email,
                        Rol rol) {
        }

        public record TipoSolicitudRequest(
                        @NotBlank String nombre,
                        String descripcion) {
        }

        public record TipoSolicitudResponse(
                        Long id,
                        String nombre,
                        String descripcion) {
        }

        public record PrioridadResponse(
                        String nivel) {
        }
}
