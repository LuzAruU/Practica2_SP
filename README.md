# Sistema de Gestión Universitaria

## Descripción
Sistema web desarrollado en Spring Boot para la gestión académica universitaria. Permite administrar estudiantes, materias e inscripciones de manera eficiente y segura.

## Funcionalidades Principales

### Gestión de Estudiantes
- Registro y gestión de datos de estudiantes
- Control de estado académico (activo/inactivo)
- Historial de materias cursadas y calificaciones
- Validación de datos personales y académicos

### Gestión de Materias
- Registro y administración de materias
- Control de cupos disponibles
- Gestión de prerequisitos
- Asignación de docentes
- Control de estado (activa/inactiva)

### Gestión de Inscripciones
- Inscripción de estudiantes a materias
- Control de estados (ACTIVA, APROBADA, REPROBADA)
- Registro y seguimiento de calificaciones
- Validación de cupos y prerequisitos

## Características del Sistema
- Interfaz REST para integración con otros sistemas
- Autenticación y autorización basada en roles
- Caché para optimización de consultas frecuentes
- Documentación automática de la API
- Validaciones de negocio automatizadas
- Manejo de transacciones y concurrencia
- Sistema de notificaciones
- Reportes y estadísticas académicas

## Tecnologías
- Backend: Java 21 + Spring Boot 3.x
- Base de datos: PostgreSQL
- Seguridad: JWT + Spring Security
- Documentación: Swagger/OpenAPI
- Caché: Redis

## Endpoints Principales

### Inscripciones
```
POST /api/inscripciones/estudiante/{id}/materia/{id}  # Crear inscripción
PUT  /api/inscripciones/{id}/estado                   # Actualizar estado
PUT  /api/inscripciones/{id}/nota                     # Registrar nota
```

## Validaciones
- Cupos disponibles
- Prerequisitos
- Estados de inscripción
- Calificaciones (0-10)
- Datos duplicados

## Seguridad
- Autenticación JWT
- Roles: ADMIN, DOCENTE, ESTUDIANTE
- Endpoints protegidos

## Documentación
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Requisitos
- Java 21
- PostgreSQL
- Redis (opcional)
- Maven

## Instalación
1. Clonar repositorio
2. Configurar `application.properties`
3. `mvn clean install`
4. `mvn spring-boot:run`
