package com.universidad.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    private NombreRol nombre;

    public Rol(NombreRol nombre) {
        this.nombre = nombre;
    }

    public enum NombreRol {
        ROL_ADMIN,
        ROL_DOCENTE,
        ROL_ESTUDIANTE
    }
}