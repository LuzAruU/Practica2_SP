package com.universidad.controller;

import com.universidad.exception.BusinessException;
import com.universidad.model.Inscripcion;
import com.universidad.model.Inscripcion.EstadoInscripcion;
import com.universidad.service.IInscripcionService;
import com.universidad.dto.InscripcionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
@Tag(name = "Inscripciones", description = "API para gestionar las inscripciones de estudiantes a materias")
public class InscripcionController {

    @Autowired
    private IInscripcionService inscripcionService;

    @Operation(summary = "Crear una nueva inscripción", description = "Crea una nueva inscripción de un estudiante a una materia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inscripción creada exitosamente", content = @Content(schema = @Schema(implementation = Inscripcion.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o error de validación"),
            @ApiResponse(responseCode = "404", description = "Estudiante o materia no encontrados")
    })
    @PostMapping("/estudiante/{estudianteId}/materia/{materiaId}")
    public ResponseEntity<Inscripcion> crearInscripcion(
            @Parameter(description = "ID del estudiante") @PathVariable Long estudianteId,
            @Parameter(description = "ID de la materia") @PathVariable Long materiaId) {
        try {
            Inscripcion inscripcion = inscripcionService.crearInscripcion(estudianteId, materiaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(inscripcion);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar estado de una inscripción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<InscripcionDTO> actualizarEstado(
            @Parameter(description = "ID de la inscripción") @PathVariable Long id,
            @Parameter(description = "Nuevo estado de la inscripción") @RequestParam EstadoInscripcion estado) {
        try {
            Inscripcion inscripcion = inscripcionService.actualizarEstado(id, estado);
            return ResponseEntity.ok(inscripcionService.mapToDTO(inscripcion));
        } catch (BusinessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar nota de una inscripción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Nota inválida"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    @PutMapping("/{id}/nota")
    public ResponseEntity<InscripcionDTO> actualizarNota(
            @Parameter(description = "ID de la inscripción") @PathVariable Long id,
            @Parameter(description = "Nueva nota") @RequestParam Double nota) {
        try {
            if (nota < 0 || nota > 10) {
                return ResponseEntity.badRequest().build();
            }
            Inscripcion inscripcion = inscripcionService.obtenerInscripcionPorId(id);
            inscripcionService.actualizarNota(id, nota);
            return ResponseEntity.ok(inscripcionService.mapToDTO(inscripcion));
        } catch (BusinessException e) {
            if (e.getMessage().contains("no existe")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Eliminar una inscripción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inscripción eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInscripcion(
            @Parameter(description = "ID de la inscripción") @PathVariable Long id) {
        try {
            inscripcionService.eliminarInscripcion(id);
            return ResponseEntity.noContent().build();
        } catch (BusinessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener una inscripción por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción encontrada"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InscripcionDTO> obtenerInscripcionPorId(@PathVariable Long id) {
        try {
            Inscripcion inscripcion = inscripcionService.obtenerInscripcionPorId(id);
            InscripcionDTO dto = null;
            if (inscripcion != null) {
                dto = new InscripcionDTO();
                dto.setId(inscripcion.getId());
                dto.setEstudianteId(inscripcion.getEstudiante() != null ? inscripcion.getEstudiante().getId() : null);
                dto.setMateriaId(inscripcion.getMateria() != null ? inscripcion.getMateria().getId() : null);
                dto.setEstado(inscripcion.getEstado() != null ? inscripcion.getEstado().name() : null);
                dto.setNota(inscripcion.getNota());
                dto.setObservaciones(inscripcion.getObservaciones());
            }
            return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener todas las inscripciones de un estudiante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inscripciones encontrada")
    })
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<InscripcionDTO>> obtenerInscripcionesPorEstudiante(@PathVariable Long estudianteId) {
        List<InscripcionDTO> inscripciones = inscripcionService.obtenerInscripcionesDTOsPorEstudiante(estudianteId);
        return ResponseEntity.ok(inscripciones);
    }

    @Operation(summary = "Obtener todas las inscripciones de una materia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inscripciones encontrada")
    })
    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<List<InscripcionDTO>> obtenerInscripcionesPorMateria(@PathVariable Long materiaId) {
        try {
            List<InscripcionDTO> inscripciones = inscripcionService.obtenerInscripcionesDTOsPorMateria(materiaId);
            return ResponseEntity.ok(inscripciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener inscripciones activas de un estudiante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inscripciones activas encontrada")
    })
    @GetMapping("/estudiante/{estudianteId}/activas")
    public ResponseEntity<List<InscripcionDTO>> obtenerInscripcionesActivasPorEstudiante(
            @PathVariable Long estudianteId) {
        List<InscripcionDTO> inscripciones = inscripcionService
                .obtenerInscripcionesDTOsActivasPorEstudiante(estudianteId);
        return ResponseEntity.ok(inscripciones);
    }
}