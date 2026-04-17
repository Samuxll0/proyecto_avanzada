package com.proyecto_avanzada.repository;

import com.proyecto_avanzada.domain.entity.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.proyecto_avanzada.domain.enums.EstadoSolicitud;
import com.proyecto_avanzada.domain.enums.NivelPrioridad;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

       @Query("SELECT s FROM Solicitud s WHERE " +
                     "(COALESCE(:estado, NULL) IS NULL OR s.estado = :estado) AND " +
                     "(COALESCE(:tipoId, NULL) IS NULL OR s.tipoSolicitud.id = :tipoId) AND " +
                     "(COALESCE(:prioridad, NULL) IS NULL OR s.prioridad = :prioridad) AND " +
                     "(COALESCE(:responsableId, NULL) IS NULL OR s.usuarioAsignado.id = :responsableId)")
       Page<Solicitud> findByFiltros(
                     @Param("estado") EstadoSolicitud estado,
                     @Param("tipoId") Long tipoId,
                     @Param("prioridad") NivelPrioridad prioridad,
                     @Param("responsableId") Long responsableId,
                     Pageable pageable);
}
