package com.universidad.controller;

import com.universidad.model.Docente;
import com.universidad.repository.DocenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/docentes")
public class DocenteController {

    @Autowired
    private DocenteRepository docenteRepository;

    @GetMapping
    public List<Docente> listarDocentes() {
        return docenteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Docente> obtenerDocente(@PathVariable Long id) {
        Optional<Docente> docente = docenteRepository.findById(id);
        return docente.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Docente crearDocente(@RequestBody Docente docente) {
        return docenteRepository.save(docente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Docente> actualizarDocente(@PathVariable Long id, @RequestBody Docente docente) {
        return docenteRepository.findById(id)
                .map(d -> {
                    d.setNroEmpleado(docente.getNroEmpleado());
                    d.setDepartamento(docente.getDepartamento());
                    // Agrega aquí otros campos si es necesario
                    return ResponseEntity.ok(docenteRepository.save(d));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDocente(@PathVariable Long id) {
        if (docenteRepository.existsById(id)) {
            docenteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}