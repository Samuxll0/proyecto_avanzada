package com.proyecto_avanzada.dto;

public class GlobalDTOs {

        public record ErrorResponse(
                        int status,
                        String message,
                        long timestamp) {
        }

        public record SuccessResponse<T>(
                        String message,
                        T data) {
        }
}
