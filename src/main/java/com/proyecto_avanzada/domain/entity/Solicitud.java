package com.proyecto_avanzada.domain.entity;

import com.proyecto_avanzada.domain.enums.CanalOrigen;
import com.proyecto_avanzada.domain.enums.EstadoSolicitud;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal_origen", nullable = false)
    private CanalOrigen canalOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_solicitud_id")
    private TipoSolicitud tipoSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad")
    private com.proyecto_avanzada.domain.enums.NivelPrioridad prioridad;

    @Column(columnDefinition = "TEXT", name = "justificacion_prioridad")
    private String justificacionPrioridad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_asignado_id")
    private Usuario usuarioAsignado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoSolicitud.REGISTRADA;
        }
    }
}
