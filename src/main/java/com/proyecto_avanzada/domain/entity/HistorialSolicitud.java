package com.proyecto_avanzada.domain.entity;

import com.proyecto_avanzada.domain.enums.EstadoSolicitud;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private EstadoSolicitud estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false)
    private EstadoSolicitud estadoNuevo;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @Column(columnDefinition = "TEXT")
    private String comentarios;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_cambio_id")
    private Usuario autorCambio;

    @PrePersist
    public void prePersist() {
        if (fechaCambio == null) {
            fechaCambio = LocalDateTime.now();
        }
    }
}
