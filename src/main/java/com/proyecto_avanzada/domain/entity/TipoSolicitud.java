package com.proyecto_avanzada.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipos_solicitud")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;
}
