package com.universidad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Getter // Genera un getter para todos los campos de la clase
@Setter // Genera un setter para todos los campos de la clase
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "materia") // Nombre de la tabla en la base de datos
// Esta clase representa una materia en el sistema de gestión de estudiantes
public class Materia implements Serializable {

    private static final long serialVersionUID = 1L;

    // Constructor to match the required signature
    public Materia(Long id, String nombreMateria, String codigoUnico) {
        this.id = id;
        this.nombreMateria = nombreMateria;
        this.codigoUnico = codigoUnico;
    }

    @Id // Anotación que indica que este campo es la clave primaria
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY) // Generación automática del ID
    @Column(name = "id_materia") // Nombre de la columna en la base de datos
    // El ID de la materia es generado automáticamente por la base de datos
    private Long id;

    @NotBlank(message = "El nombre de la materia es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(name = "nombre_materia", nullable = false, length = 100) // Columna no nula con longitud máxima de 100
                                                                     // caracteres
    // El nombre de la materia no puede ser nulo y tiene una longitud máxima de 100
    // caracteres
    private String nombreMateria;

    @NotBlank(message = "El código único es obligatorio")
    @Size(min = 3, max = 20, message = "El código debe tener entre 3 y 20 caracteres")
    @Column(name = "codigo_unico", nullable = false, unique = true) // Columna no nula y con valor único
    // El código único de la materia no puede ser nulo y debe ser único en la base
    // de datos
    private String codigoUnico;

    @NotNull(message = "Los créditos son obligatorios")
    @Min(value = 1, message = "Los créditos deben ser mayores a 0")
    @Column(name = "creditos", nullable = false) // Columna no nula
    // El número de créditos de la materia no puede ser nulo
    private Integer creditos;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "cupo_maximo", nullable = false)
    @Min(value = 1, message = "El cupo máximo debe ser mayor a 0")
    private Integer cupoMaximo;

    @Column(name = "activa", nullable = false)
    private boolean activa = true;

    @Version // Anotación para manejar la versión de la entidad
    private Long version; // Campo para manejar la versión de la entidad, útil para el control de
                          // concurrencia

    /**
     * Lista de materias que son prerequisitos para esta materia.
     */
    @ManyToMany
    @JoinTable(name = "materia_prerequisito", joinColumns = @JoinColumn(name = "id_materia"), inverseJoinColumns = @JoinColumn(name = "id_prerequisito") // Nombre
                                                                                                                                                         // de
                                                                                                                                                         // la
                                                                                                                                                         // columna
                                                                                                                                                         // en
                                                                                                                                                         // la
                                                                                                                                                         // tabla
                                                                                                                                                         // inversa
    )
    private List<Materia> prerequisitos;

    /**
     * Lista de materias para las que esta materia es prerequisito.
     */
    @ManyToMany(mappedBy = "prerequisitos")
    private List<Materia> esPrerequisitoDe;

    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Docente docente;

    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL)
    private Set<Inscripcion> inscripciones = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (cupoMaximo == null) {
            cupoMaximo = 30; // Valor por defecto
        }
    }

    public boolean tieneCupoDisponible() {
        long inscripcionesActivas = inscripciones.stream()
                .filter(i -> i.getEstado() == Inscripcion.EstadoInscripcion.ACTIVA)
                .count();
        return inscripcionesActivas < cupoMaximo;
    }

    /**
     * Verifica si agregar la materia con el ID dado como prerequisito formaría un
     * ciclo.
     * 
     * @param prerequisitoId ID de la materia candidata a prerequisito
     * @return true si se formaría un ciclo, false en caso contrario
     */
    public boolean formariaCirculo(Long prerequisitoId) {
        return formariaCirculoRecursivo(this.getId(), prerequisitoId, new java.util.HashSet<>());
    }

    // Método auxiliar recursivo para detectar ciclos
    private boolean formariaCirculoRecursivo(Long objetivoId, Long actualId, java.util.Set<Long> visitados) {
        if (objetivoId == null || actualId == null)
            return false;
        if (objetivoId.equals(actualId))
            return true;
        if (!visitados.add(actualId))
            return false;
        if (this.getPrerequisitos() == null)
            return false;
        for (Materia prereq : this.getPrerequisitos()) { // Itera sobre los prerequisitos de la materia
            if (prereq != null && prereq.getId() != null && prereq.getId().equals(actualId)) { // Verifica si el
                                                                                               // prerequisito actual es
                                                                                               // el objetivo
                if (prereq.getPrerequisitos() != null) { // Verifica si tiene prerequisitos
                    // Si el prerequisito tiene prerequisitos, verifica recursivamente si alguno de
                    // ellos forma un ciclo
                    for (Materia subPrereq : prereq.getPrerequisitos()) { // Itera sobre los prerequisitos del
                                                                          // prerequisito actual
                        if (formariaCirculoRecursivo(objetivoId, subPrereq.getId(), visitados)) { // Llama
                                                                                                  // recursivamente al
                                                                                                  // método
                            // Si se encuentra un ciclo, retorna true
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

}