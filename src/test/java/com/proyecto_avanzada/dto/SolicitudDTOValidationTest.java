package com.proyecto_avanzada.dto;

import com.proyecto_avanzada.dto.SolicitudDTOs;
import com.proyecto_avanzada.domain.enums.CanalOrigen;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudDTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void solicitudRequest_DebeFallar_SiDescripionEstaVacia() {
        SolicitudDTOs.SolicitudRequest request = new SolicitudDTOs.SolicitudRequest("", CanalOrigen.CSU);
        Set<ConstraintViolation<SolicitudDTOs.SolicitudRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("descripcion")));
    }

    @Test
    void solicitudRequest_DebeFallar_SiCanalOrigenEsNull() {
        SolicitudDTOs.SolicitudRequest request = new SolicitudDTOs.SolicitudRequest("Ayuda", null);
        Set<ConstraintViolation<SolicitudDTOs.SolicitudRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("canalOrigen")));
    }

    @Test
    void clasificacionRequest_DebeFallar_SiFaltanCampos() {
        SolicitudDTOs.ClasificacionRequest request = new SolicitudDTOs.ClasificacionRequest(null, null, " ");
        Set<ConstraintViolation<SolicitudDTOs.ClasificacionRequest>> violations = validator.validate(request);

        assertEquals(3, violations.size());
    }
}
