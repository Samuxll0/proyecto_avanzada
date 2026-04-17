package com.proyecto_avanzada.repository;

import com.proyecto_avanzada.domain.entity.HistorialSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialSolicitudRepository extends JpaRepository<HistorialSolicitud, Long> {
    List<HistorialSolicitud> findBySolicitudIdOrderByFechaCambioDesc(Long solicitudId);
}
