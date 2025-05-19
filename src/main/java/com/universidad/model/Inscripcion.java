package com.universidad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inscripciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    @NotNull
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id", nullable = false)
    @NotNull
    private Materia materia;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoInscripcion estado;

    @Column(name = "nota")
    private Double nota;

    @Column(name = "observaciones")
    private String observaciones;

    public enum EstadoInscripcion {
        ACTIVA,
        APROBADA,
        REPROBADA,
        CANCELADA
    }

    @PrePersist
    protected void onCreate() {
        fechaInscripcion = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoInscripcion.ACTIVA;
        }
    }
}