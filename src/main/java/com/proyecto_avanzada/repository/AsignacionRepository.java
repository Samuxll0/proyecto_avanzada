package com.proyecto_avanzada.repository;

import com.proyecto_avanzada.domain.entity.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    List<Asignacion> findBySolicitudId(Long solicitudId);
}
