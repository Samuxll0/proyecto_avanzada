package com.proyecto_avanzada.repository;

import com.proyecto_avanzada.domain.entity.ComentarioSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioSolicitudRepository extends JpaRepository<ComentarioSolicitud, Long> {
    List<ComentarioSolicitud> findBySolicitudIdOrderByFechaCreacionAsc(Long solicitudId);
}
