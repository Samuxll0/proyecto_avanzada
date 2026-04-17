package com.proyecto_avanzada.service;

import com.proyecto_avanzada.domain.entity.*;
import com.proyecto_avanzada.domain.enums.EstadoSolicitud;
import com.proyecto_avanzada.domain.enums.NivelPrioridad;
import com.proyecto_avanzada.dto.SolicitudDTOs;
import com.proyecto_avanzada.repository.*;
import com.proyecto_avanzada.service.AIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TipoSolicitudRepository tipoSolicitudRepository;
    @Mock
    private AsignacionRepository asignacionRepository;
    @Mock
    private HistorialSolicitudRepository historialRepository;
    @Mock
    private AIService aiService;

    @InjectMocks
    private SolicitudService solicitudService;

    private Usuario solicitante;
    private Solicitud solicitud;

    @BeforeEach
    void setUp() {
        solicitante = new Usuario();
        solicitante.setId(1L);
        solicitante.setEmail("estudiante@triagre.com");
        solicitante.setActivo(true);

        solicitud = new Solicitud();
        solicitud.setId(1L);
        solicitud.setSolicitante(solicitante);
        solicitud.setEstado(EstadoSolicitud.REGISTRADA);
    }

    @Test
    void crearSolicitud_DebeTenerExito() {
        SolicitudDTOs.SolicitudRequest request = new SolicitudDTOs.SolicitudRequest("Problema con cupo",
                com.proyecto_avanzada.domain.enums.CanalOrigen.CSU);

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(solicitante));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(i -> i.getArguments()[0]);

        SolicitudDTOs.SolicitudResponse response = solicitudService.crearSolicitud(request, solicitante.getEmail());

        assertNotNull(response);
        assertEquals(EstadoSolicitud.REGISTRADA, response.estado());
        verify(solicitudRepository).save(any(Solicitud.class));
        verify(historialRepository).save(any(HistorialSolicitud.class));
    }

    @Test
    void clasificarSolicitud_DebeCambiarEstadoAClasificada() {
        TipoSolicitud tipo = new TipoSolicitud();
        tipo.setId(1L);

        SolicitudDTOs.ClasificacionRequest request = new SolicitudDTOs.ClasificacionRequest(1L, NivelPrioridad.ALTA,
                "Justificación...");

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(tipoSolicitudRepository.findById(1L)).thenReturn(Optional.of(tipo));
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(solicitante));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(i -> i.getArguments()[0]);

        SolicitudDTOs.SolicitudResponse response = solicitudService.clasificarSolicitud(1L, request,
                "admin@triagre.com");

        assertEquals(EstadoSolicitud.CLASIFICADA, response.estado());
        assertEquals(NivelPrioridad.ALTA, response.prioridad());
    }

    @Test
    void clasificarSolicitud_DebeFallar_SiEstadoNoEsRegistrada() {
        solicitud.setEstado(EstadoSolicitud.EN_ATENCION);
        SolicitudDTOs.ClasificacionRequest request = new SolicitudDTOs.ClasificacionRequest(1L, NivelPrioridad.ALTA,
                "...");

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

        assertThrows(ResponseStatusException.class,
                () -> solicitudService.clasificarSolicitud(1L, request, "admin@triagre.com"));
    }

    @Test
    void asignarSolicitud_DebeVincularUsuarioYCambiarEstado() {
        solicitud.setEstado(EstadoSolicitud.CLASIFICADA);
        Usuario responsable = new Usuario();
        responsable.setId(2L);
        responsable.setActivo(true);
        responsable.setEmail("docente@triagre.com");

        SolicitudDTOs.AsignacionRequest request = new SolicitudDTOs.AsignacionRequest(2L);

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(responsable));
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(solicitante));

        solicitudService.asignarSolicitud(1L, request, "admin@triagre.com");

        assertEquals(EstadoSolicitud.EN_ATENCION, solicitud.getEstado());
        assertEquals(responsable, solicitud.getUsuarioAsignado());
        verify(asignacionRepository).save(any(Asignacion.class));
    }

    @Test
    void obtenerSolicitudModificable_DebeLanzarExcepcion_SiEstaCerrada() {
        solicitud.setEstado(EstadoSolicitud.CERRADA);
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

        assertThrows(ResponseStatusException.class, () -> solicitudService.atenderSolicitud(1L,
                new SolicitudDTOs.AtencionRequest("Completado"), "admin@triagre.com"));
    }
}
