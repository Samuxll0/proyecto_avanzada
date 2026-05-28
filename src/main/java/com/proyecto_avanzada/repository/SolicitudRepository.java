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
                     "(:estado IS NULL OR s.estado = :estado) AND " +
                     "(:tipoId IS NULL OR s.tipoSolicitud.id = :tipoId) AND " +
                     "(:prioridad IS NULL OR s.prioridad = :prioridad) AND " +
                     "(:responsableId IS NULL OR s.usuarioAsignado.id = :responsableId) AND " +
                     "(:emailSolicitante IS NULL OR s.solicitante.email = :emailSolicitante) AND " +
                     "(:search IS NULL OR LOWER(s.descripcion) LIKE LOWER(CONCAT('%', :search, '%')))")
       Page<Solicitud> findByFiltros(
                     @Param("estado") EstadoSolicitud estado,
                     @Param("tipoId") Long tipoId,
                     @Param("prioridad") NivelPrioridad prioridad,
                     @Param("responsableId") Long responsableId,
                     @Param("emailSolicitante") String emailSolicitante,
                     @Param("search") String search,
                     Pageable pageable);

       long countByEstado(EstadoSolicitud estado);

       long countBySolicitanteEmail(String email);

       long countBySolicitanteEmailAndEstado(String email, EstadoSolicitud estado);
}
