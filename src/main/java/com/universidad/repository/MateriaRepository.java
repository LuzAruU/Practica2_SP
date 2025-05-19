package com.universidad.repository;

import com.universidad.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    Materia findByCodigoUnico(String codigoUnico);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // Bloqueo pesimista para evitar condiciones de carrera
    Optional<Materia> findById(Long id);

    // Método sin lock para lecturas normales
    Optional<Materia> findMateriaById(Long id);

    List<Materia> findByDocenteId(Long docenteId);
}
