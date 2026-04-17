package com.proyecto_avanzada.controller;

import com.proyecto_avanzada.domain.enums.NivelPrioridad;
import com.proyecto_avanzada.dto.SolicitudDTOs;
import com.proyecto_avanzada.service.SolicitudService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integración del controlador de solicitudes.
 * Usamos @SpringBootTest para garantizar que todo el contexto de seguridad y
 * los inyectores de parámetros (Authentication) funcionen correctamente.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SolicitudService solicitudService;

    @Test
    @WithMockUser(username = "estudiante@triagre.com", roles = "ESTUDIANTE")
    void crear_DebePermitirAEstudiante() throws Exception {
        SolicitudDTOs.SolicitudRequest request = new SolicitudDTOs.SolicitudRequest("Ayuda",
                com.proyecto_avanzada.domain.enums.CanalOrigen.CSU);

        mockMvc.perform(post("/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(solicitudService).crearSolicitud(any(), eq("estudiante@triagre.com"));
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    void clasificar_DebeDenegarAEstudiante() throws Exception {
        SolicitudDTOs.ClasificacionRequest request = new SolicitudDTOs.ClasificacionRequest(1L, NivelPrioridad.ALTA,
                "...");

        mockMvc.perform(put("/solicitudes/1/clasificacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@triagre.com", roles = "COORDINADOR")
    void clasificar_DebePermitirACoordinador() throws Exception {
        SolicitudDTOs.ClasificacionRequest request = new SolicitudDTOs.ClasificacionRequest(1L, NivelPrioridad.ALTA,
                "...");

        mockMvc.perform(put("/solicitudes/1/clasificacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(solicitudService).clasificarSolicitud(eq(1L), any(), eq("admin@triagre.com"));
    }
}
