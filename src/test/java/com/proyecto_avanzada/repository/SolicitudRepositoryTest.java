package com.proyecto_avanzada.repository;

import com.proyecto_avanzada.domain.entity.Solicitud;
import com.proyecto_avanzada.domain.entity.Usuario;
import com.proyecto_avanzada.domain.enums.CanalOrigen;
import com.proyecto_avanzada.domain.enums.EstadoSolicitud;
import com.proyecto_avanzada.domain.enums.NivelPrioridad;
import com.proyecto_avanzada.domain.enums.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SolicitudRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SolicitudRepository solicitudRepository;

    private Usuario solicitante;

    @BeforeEach
    void setUp() {
        solicitante = Usuario.builder()
                .nombre("Juan")
                .email("juan@estudiante.triagre.com")
                .password("123")
                .rol(Rol.ESTUDIANTE)
                .activo(true)
                .build();
        entityManager.persist(solicitante);

        // Crear algunas solicitudes de prueba
        for (int i = 0; i < 5; i++) {
            Solicitud s = Solicitud.builder()
                    .descripcion("Solicitud " + i)
                    .canalOrigen(CanalOrigen.CSU)
                    .estado(i < 3 ? EstadoSolicitud.REGISTRADA : EstadoSolicitud.CLASIFICADA)
                    .prioridad(i < 3 ? NivelPrioridad.MEDIA : NivelPrioridad.ALTA)
                    .solicitante(solicitante)
                    .build();
            entityManager.persist(s);
        }
        entityManager.flush();
    }

    @Test
    void findByFiltros_DebeFiltrarPorEstado() {
        Page<Solicitud> result = solicitudRepository.findByFiltros(
                EstadoSolicitud.REGISTRADA, null, null, null, PageRequest.of(0, 10));

        assertEquals(3, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(s -> s.getEstado() == EstadoSolicitud.REGISTRADA));
    }

    @Test
    void findByFiltros_DebeFiltrarPorPrioridad() {
        Page<Solicitud> result = solicitudRepository.findByFiltros(
                null, null, NivelPrioridad.ALTA, null, PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(s -> s.getPrioridad() == NivelPrioridad.ALTA));
    }

    @Test
    void findByFiltros_DebeRetornarTodos_SiFiltrosSonNull() {
        Page<Solicitud> result = solicitudRepository.findByFiltros(
                null, null, null, null, PageRequest.of(0, 10));

        assertEquals(5, result.getTotalElements());
    }
}
