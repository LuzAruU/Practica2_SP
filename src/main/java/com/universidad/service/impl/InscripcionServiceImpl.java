package com.universidad.service.impl;

import com.universidad.exception.BusinessException;
import com.universidad.model.Estudiante;
import com.universidad.model.Inscripcion;
import com.universidad.model.Inscripcion.EstadoInscripcion;
import com.universidad.model.Materia;
import com.universidad.repository.EstudianteRepository;
import com.universidad.repository.InscripcionRepository;
import com.universidad.repository.MateriaRepository;
import com.universidad.service.IInscripcionService;
import com.universidad.dto.InscripcionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InscripcionServiceImpl implements IInscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Override
    @Transactional
    @CacheEvict(value = { "inscripciones", "materias", "estudiantes" }, allEntries = true)
    public Inscripcion crearInscripcion(Long estudianteId, Long materiaId) {
        // Validar que el estudiante existe y está activo
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new BusinessException("Estudiante no encontrado"));

        if (!estudiante.isActivo()) {
            throw new BusinessException("El estudiante no está activo");
        }

        // Validar que la materia existe y está activa
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new BusinessException("Materia no encontrada"));

        if (!materia.isActiva()) {
            throw new BusinessException("La materia no está activa");
        }

        // Validar cupo disponible
        if (!validarCupoDisponible(materiaId)) {
            throw new BusinessException("No hay cupos disponibles para esta materia");
        }

        // Validar inscripción existente
        if (validarInscripcionExistente(estudianteId, materiaId)) {
            throw new BusinessException("El estudiante ya está inscrito en esta materia");
        }

        // Validar prerequisitos
        if (!validarPrerequisitos(estudianteId, materiaId)) {
            throw new BusinessException("No cumple con los prerequisitos de la materia");
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setEstudiante(estudiante);
        inscripcion.setMateria(materia);
        inscripcion.setEstado(EstadoInscripcion.ACTIVA);

        return inscripcionRepository.save(inscripcion);
    }

    @Override
    @Transactional
    @CacheEvict(value = "inscripciones", allEntries = true)
    public Inscripcion actualizarEstado(Long inscripcionId, EstadoInscripcion nuevoEstado) {
        Inscripcion inscripcion = obtenerInscripcionPorId(inscripcionId);
        inscripcion.setEstado(nuevoEstado);
        return inscripcionRepository.save(inscripcion);
    }

    @Override
    @Transactional
    @CacheEvict(value = "inscripciones", allEntries = true)
    public Inscripcion actualizarNota(Long inscripcionId, Double nota) throws BusinessException {
        Inscripcion inscripcion = obtenerInscripcionPorId(inscripcionId);
        if (nota < 0 || nota > 10) {
            throw new BusinessException("La nota debe estar entre 0 y 10.");
        }
        inscripcion.setNota(nota);

        // Actualizar estado basado en la nota
        if (nota >= 6) {
            inscripcion.setEstado(EstadoInscripcion.APROBADA);
        } else {
            inscripcion.setEstado(EstadoInscripcion.REPROBADA);
        }

        return inscripcionRepository.save(inscripcion);
    }

    @Override
    @Transactional
    @CacheEvict(value = "inscripciones", allEntries = true)
    public void eliminarInscripcion(Long inscripcionId) {
        Inscripcion inscripcion = obtenerInscripcionPorId(inscripcionId);
        inscripcion.setEstado(EstadoInscripcion.CANCELADA);
        inscripcionRepository.save(inscripcion);
    }

    @Override
    @Cacheable(value = "inscripciones", key = "#id")
    public Inscripcion obtenerInscripcionPorId(Long id) throws BusinessException {
        return inscripcionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("La inscripción con ID " + id + " no existe."));
    }

    @Override
    @Cacheable(value = "inscripciones", key = "'estudiante_' + #estudianteId")
    public List<Inscripcion> obtenerInscripcionesPorEstudiante(Long estudianteId) {
        return inscripcionRepository.findByEstudianteId(estudianteId);
    }

    @Override
    @Cacheable(value = "inscripciones", key = "'materia_' + #materiaId")
    public List<InscripcionDTO> obtenerInscripcionesDTOsPorMateria(Long materiaId) {
        List<Inscripcion> inscripciones = inscripcionRepository.findByMateriaId(materiaId);
        return inscripciones.stream().map(this::mapToDTO).collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Cacheable(value = "inscripciones", key = "'estudiante_' + #estudianteId")
    public List<InscripcionDTO> obtenerInscripcionesDTOsPorEstudiante(Long estudianteId) {
        List<Inscripcion> inscripciones = inscripcionRepository.findByEstudianteId(estudianteId);
        return inscripciones.stream().map(this::mapToDTO).collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Cacheable(value = "inscripciones", key = "'estudiante_activas_' + #estudianteId")
    public List<InscripcionDTO> obtenerInscripcionesDTOsActivasPorEstudiante(Long estudianteId) {
        List<Inscripcion> inscripciones = inscripcionRepository.findByEstudianteIdAndEstado(estudianteId,
                EstadoInscripcion.ACTIVA);
        return inscripciones.stream().map(this::mapToDTO).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public InscripcionDTO mapToDTO(Inscripcion inscripcion) {
        InscripcionDTO dto = new InscripcionDTO();
        dto.setId(inscripcion.getId());
        dto.setEstudianteId(inscripcion.getEstudiante() != null ? inscripcion.getEstudiante().getId() : null);
        dto.setMateriaId(inscripcion.getMateria() != null ? inscripcion.getMateria().getId() : null);
        dto.setEstado(inscripcion.getEstado() != null ? inscripcion.getEstado().name() : null);
        dto.setNota(inscripcion.getNota());
        dto.setObservaciones(inscripcion.getObservaciones());
        return dto;
    }

    @Override
    @Cacheable(value = "inscripciones", key = "'estudiante_activas_' + #estudianteId")
    public List<Inscripcion> obtenerInscripcionesActivasPorEstudiante(Long estudianteId) {
        return inscripcionRepository.findByEstudianteIdAndEstado(estudianteId, EstadoInscripcion.ACTIVA);
    }

    @Override
    public boolean validarCupoDisponible(Long materiaId) {
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new BusinessException("Materia no encontrada"));
        return materia.tieneCupoDisponible();
    }

    @Override
    public boolean validarInscripcionExistente(Long estudianteId, Long materiaId) {
        return inscripcionRepository.existsByEstudianteIdAndMateriaIdAndEstado(
                estudianteId, materiaId, EstadoInscripcion.ACTIVA);
    }

    @Override
    public boolean validarPrerequisitos(Long estudianteId, Long materiaId) {
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new BusinessException("Materia no encontrada"));

        // Si la materia no tiene prerequisitos, retornar true
        if (materia.getPrerequisitos() == null || materia.getPrerequisitos().isEmpty()) {
            return true;
        }

        // Obtener todas las materias aprobadas del estudiante
        List<Inscripcion> inscripcionesAprobadas = inscripcionRepository
                .findByEstudianteIdAndEstado(estudianteId, EstadoInscripcion.APROBADA);

        // Verificar que el estudiante haya aprobado todos los prerequisitos
        return materia.getPrerequisitos().stream()
                .allMatch(prerequisito -> inscripcionesAprobadas.stream()
                        .anyMatch(inscripcion -> inscripcion.getMateria().getId().equals(prerequisito.getId())));
    }

    @Override
    public List<Inscripcion> obtenerInscripcionesPorMateria(Long materiaId) {
        return inscripcionRepository.findByMateriaId(materiaId);
    }
}