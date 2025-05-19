package com.universidad.service;

import com.universidad.model.Inscripcion;
import com.universidad.model.Inscripcion.EstadoInscripcion;
import com.universidad.dto.InscripcionDTO;

import java.util.List;

public interface IInscripcionService {

    Inscripcion crearInscripcion(Long estudianteId, Long materiaId);

    Inscripcion actualizarEstado(Long inscripcionId, EstadoInscripcion nuevoEstado);

    Inscripcion actualizarNota(Long inscripcionId, Double nota);

    void eliminarInscripcion(Long inscripcionId);

    Inscripcion obtenerInscripcionPorId(Long id);

    List<Inscripcion> obtenerInscripcionesPorEstudiante(Long estudianteId);

    List<Inscripcion> obtenerInscripcionesPorMateria(Long materiaId);

    List<Inscripcion> obtenerInscripcionesActivasPorEstudiante(Long estudianteId);

    boolean validarCupoDisponible(Long materiaId);

    boolean validarInscripcionExistente(Long estudianteId, Long materiaId);

    boolean validarPrerequisitos(Long estudianteId, Long materiaId);

    List<InscripcionDTO> obtenerInscripcionesDTOsPorMateria(Long materiaId);

    List<InscripcionDTO> obtenerInscripcionesDTOsPorEstudiante(Long estudianteId);

    List<InscripcionDTO> obtenerInscripcionesDTOsActivasPorEstudiante(Long estudianteId);

    InscripcionDTO mapToDTO(Inscripcion inscripcion);
}