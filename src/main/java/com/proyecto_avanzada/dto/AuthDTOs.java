package com.proyecto_avanzada.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDTOs {
    public record LoginRequest(@NotBlank String email, @NotBlank String password) {
    }

    public record LoginResponse(String token) {
    }

    public record RegisterRequest(
            @NotBlank String nombre,
            @NotBlank String email,
            @NotBlank String password) {
    }
}
