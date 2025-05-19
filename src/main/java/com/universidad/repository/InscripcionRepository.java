package com.universidad.repository;

import com.universidad.model.Inscripcion;
import com.universidad.model.Inscripcion.EstadoInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    List<Inscripcion> findByEstudianteId(Long estudianteId);

    List<Inscripcion> findByMateriaId(Long materiaId);

    List<Inscripcion> findByEstudianteIdAndEstado(Long estudianteId, EstadoInscripcion estado);

    @Query("SELECT i FROM Inscripcion i WHERE i.estudiante.id = ?1 AND i.materia.id = ?2 AND i.estado = 'ACTIVA'")
    Optional<Inscripcion> findInscripcionActiva(Long estudianteId, Long materiaId);

    @Query("SELECT COUNT(i) FROM Inscripcion i WHERE i.materia.id = ?1 AND i.estado = 'ACTIVA'")
    Long countInscripcionesActivasByMateria(Long materiaId);

    boolean existsByEstudianteIdAndMateriaIdAndEstado(Long estudianteId, Long materiaId, EstadoInscripcion estado);
}