package com.proyecto_avanzada.dto;

import jakarta.validation.constraints.Size;

public class AuthDTOs {
    public record LoginRequest(@jakarta.validation.constraints.NotBlank String email, @jakarta.validation.constraints.NotBlank String password) {
    }

    public record LoginResponse(String token) {
    }

    public record RegisterRequest(
            @jakarta.validation.constraints.NotBlank String nombre,
            @jakarta.validation.constraints.NotBlank String email,
            @jakarta.validation.constraints.NotBlank String password) {
    }

    public record CambiarPasswordRequest(
            String nombre,
            @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String nuevaPassword) {
    }
}
