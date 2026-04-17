package com.proyecto_avanzada.config;

import com.proyecto_avanzada.domain.entity.Usuario;
import com.proyecto_avanzada.domain.enums.Rol;
import com.proyecto_avanzada.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.proyecto_avanzada.domain.entity.TipoSolicitud;
import com.proyecto_avanzada.repository.TipoSolicitudRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final TipoSolicitudRepository tipoSolicitudRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            Usuario admin = Usuario.builder()
                    .nombre("Coordinador Global")
                    .email("admin@coordinacion.triagre.com")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.COORDINADOR)
                    .activo(true)
                    .build();
            usuarioRepository.save(admin);
            System.out.println(
                    "====== SEEDER EXECUTED: Usuario Coordinador Creado (admin@coordinacion.triagre.com / admin123) ======");
        }

        if (tipoSolicitudRepository.count() == 0) {
            List<TipoSolicitud> tipos = List.of(
                    TipoSolicitud.builder().nombre("Registro de asignaturas")
                            .descripcion("Solicitud para registrar materias del semestre").build(),
                    TipoSolicitud.builder().nombre("Homologación")
                            .descripcion("Reconocimiento de asignaturas previamente cursadas").build(),
                    TipoSolicitud.builder().nombre("Cancelación de asignaturas")
                            .descripcion("Retiro de asignaturas inscritas activas").build(),
                    TipoSolicitud.builder().nombre("Solicitud de cupos")
                            .descripcion("Apertura o petición de cupos en asignaturas").build(),
                    TipoSolicitud.builder().nombre("Consulta académica")
                            .descripcion("Peticiones o dudas generales sobre normativas").build());
            tipoSolicitudRepository.saveAll(tipos);
            System.out.println("====== SEEDER EXECUTED: 5 Tipos Básicos (RF-02) Creados ======");
        }
    }
}
