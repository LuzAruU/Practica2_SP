# Sistema de Gestión Universitaria

## Descripción
Sistema de gestión universitaria que permite administrar estudiantes, profesores, materias e inscripciones.

## Tecnologías Utilizadas
- Java 21
- Spring Boot 3.2.3
- PostgreSQL
- Redis (Caché)
- JWT (Autenticación)
- Swagger (Documentación API)

## Configuración del Entorno

### Requisitos Previos
- Java 21 o superior
- PostgreSQL 12 o superior
- Redis 6 o superior
- Maven 3.8 o superior

### Configuración de la Base de Datos
1. Crear base de datos PostgreSQL:
```sql
CREATE DATABASE universidad;
```

2. Configurar credenciales en `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/universidad
spring.datasource.username=udev
spring.datasource.password=1234
```

### Configuración de Redis
```properties
spring.redis.host=localhost
spring.redis.port=6379
```

## Roles y Permisos

### 1. ADMIN
- Gestión completa de usuarios
- Gestión de materias
- Asignación de profesores
- Acceso a todos los endpoints

### 2. DOCENTE
- Ver inscripciones de sus materias
- Actualizar notas
- Ver listado de estudiantes
- Endpoints permitidos:
  - GET `/api/inscripciones/materia/{materiaId}`
  - PUT `/api/inscripciones/{id}/nota`
  - GET `/api/materias/profesor/{profesorId}`

### 3. ESTUDIANTE
- Ver materias disponibles
- Inscribirse a materias
- Ver sus inscripciones
- Endpoints permitidos:
  - GET `/api/materias`
  - POST `/api/inscripciones/estudiante/{estudianteId}/materia/{materiaId}`
  - GET `/api/inscripciones/estudiante/{estudianteId}`

## Endpoints Principales

### Autenticación
- POST `/api/auth/login` - Login de usuario
- POST `/api/auth/refresh` - Renovar token JWT

### Usuarios
- GET `/api/usuarios` - Listar usuarios (ADMIN)
- POST `/api/usuarios` - Crear usuario (ADMIN)
- PUT `/api/usuarios/{id}` - Actualizar usuario (ADMIN)
- DELETE `/api/usuarios/{id}` - Eliminar usuario (ADMIN)

### Materias
- GET `/api/materias` - Listar materias
- POST `/api/materias` - Crear materia (ADMIN)
- PUT `/api/materias/{id}` - Actualizar materia (ADMIN)
- DELETE `/api/materias/{id}` - Eliminar materia (ADMIN)
- PUT `/api/materias/{id}/profesor` - Asignar profesor (ADMIN)

### Inscripciones
- POST `/api/inscripciones/estudiante/{estudianteId}/materia/{materiaId}` - Crear inscripción
- PUT `/api/inscripciones/{id}/estado` - Actualizar estado
- PUT `/api/inscripciones/{id}/nota` - Actualizar nota
- GET `/api/inscripciones/estudiante/{estudianteId}` - Ver inscripciones de estudiante
- GET `/api/inscripciones/materia/{materiaId}` - Ver inscripciones de materia

## Caché
El sistema utiliza Redis para el caché con las siguientes configuraciones:
- Inscripciones: 30 minutos
- Materias: 60 minutos
- Estudiantes: 45 minutos

## Documentación API
La documentación completa de la API está disponible en:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`


### Credenciales de Prueba
1. Administrador:
   - Email: admin@universidad.com
   - User: admin
   - Password: admin123

2. Profesor:
   - Email: profesor1@universidad.com
   - User: docente
   - Password: admin123

3. Estudiante:
   - Email: estudiante1@universidad.com
   - User: estudiante
   - Password: admin123

## Manejo de Errores
El sistema implementa un manejador global de excepciones que proporciona respuestas estructuradas para:
- Errores de validación
- Errores de autenticación
- Errores de base de datos
- Errores generales

## Seguridad
- Autenticación mediante JWT
- Tokens con expiración de 24 horas
- Endpoints protegidos por roles
- Validación de datos de entrada


## 📚 Endpoints Detallados

### 🔐 Autenticación

#### POST `/api/auth/login`
- **Descripción:** Inicia sesión y retorna un token JWT.
- **Validaciones:**
  - `username`: obligatorio, formato email válido
  - `password`: obligatorio
- **Respuesta:** Token JWT y datos del usuario.
image.png

#### POST `/api/auth/refresh`
- **Descripción:** Renueva el token JWT si el actual está por expirar.
- **Validaciones:**
  - Token JWT válido en el header
- **Respuesta:** Nuevo token JWT.

### 👤 Usuarios

#### GET `/api/usuarios`
- **Descripción:** Lista todos los usuarios registrados.
- **Validaciones:**
  - Solo accesible por usuarios con rol ADMIN
- **Respuesta:** Lista de usuarios.

#### POST `/api/usuarios`
- **Descripción:** Crea un nuevo usuario.
- **Validaciones:**
  - `nombre`: obligatorio, no vacío
  - `apellido`: obligatorio, no vacío
  - `email`: obligatorio, formato email válido, único
  - `username`: obligatorio, único
  - `password`: obligatorio, mínimo 6 caracteres
  - `roles`: al menos uno
- **Respuesta:** Usuario creado.

#### PUT `/api/usuarios/{id}`
- **Descripción:** Actualiza los datos de un usuario existente.
- **Validaciones:**
  - `id`: debe existir
  - Campos como en POST, pero pueden ser opcionales según implementación
- **Respuesta:** Usuario actualizado.

#### DELETE `/api/usuarios/{id}`
- **Descripción:** Elimina (o desactiva) un usuario.
- **Validaciones:**
  - `id`: debe existir
- **Respuesta:** Sin contenido (204) o mensaje de éxito.

### 📚 Materias

#### GET `/api/materias`
- **Descripción:** Lista todas las materias disponibles.
- **Validaciones:** Ninguna
- **Respuesta:** Lista de materias.

#### POST `/api/materias`
- **Descripción:** Crea una nueva materia.
- **Validaciones:**
  - `nombre`: obligatorio, único
  - `descripcion`: obligatorio
  - `cupoMaximo`: obligatorio, mayor a 0
- **Respuesta:** Materia creada.

#### PUT `/api/materias/{id}`
- **Descripción:** Actualiza los datos de una materia.
- **Validaciones:**
  - `id`: debe existir
  - Campos como en POST
- **Respuesta:** Materia actualizada.

#### DELETE `/api/materias/{id}`
- **Descripción:** Elimina una materia.
- **Validaciones:**
  - `id`: debe existir
- **Respuesta:** Sin contenido (204) o mensaje de éxito.

#### PUT `/api/materias/{id}/profesor`
- **Descripción:** Asigna un profesor a una materia.
- **Validaciones:**
  - `id`: debe existir
  - `profesorId`: debe existir y estar activo
- **Respuesta:** Materia con profesor asignado.

#### GET `/api/materias/profesor/{profesorId}`
- **Descripción:** Lista materias asignadas a un profesor.
- **Validaciones:**
  - `profesorId`: debe existir
- **Respuesta:** Lista de materias.


### 📝 Inscripciones

#### POST `/api/inscripciones/estudiante/{estudianteId}/materia/{materiaId}`
- **Descripción:** Inscribe a un estudiante en una materia.
- **Validaciones:**
  - `estudianteId`: debe existir y estar activo
  - `materiaId`: debe existir y estar activa
  - No debe estar ya inscrito
  - Debe haber cupo disponible
  - Debe cumplir con los prerrequisitos
- **Respuesta:** Inscripción creada.

#### PUT `/api/inscripciones/{id}/estado`
- **Descripción:** Actualiza el estado de una inscripción.
- **Validaciones:**
  - `id`: debe existir
  - `estado`: valor permitido (ACTIVA, APROBADA, REPROBADA, CANCELADA)
- **Respuesta:** Inscripción actualizada.

#### PUT `/api/inscripciones/{id}/nota`
- **Descripción:** Actualiza la nota de una inscripción.
- **Validaciones:**
  - `id`: debe existir
  - `nota`: entre 0 y 10
- **Respuesta:** Inscripción con nota actualizada.

#### DELETE `/api/inscripciones/{id}`
- **Descripción:** Elimina (o cancela) una inscripción.
- **Validaciones:**
  - `id`: debe existir
- **Respuesta:** Sin contenido (204) o mensaje de éxito.

#### GET `/api/inscripciones/{id}`
- **Descripción:** Obtiene una inscripción por su ID.
- **Validaciones:**
  - `id`: debe existir
- **Respuesta:** Detalle de la inscripción.

#### GET `/api/inscripciones/estudiante/{estudianteId}`
- **Descripción:** Lista todas las inscripciones de un estudiante.
- **Validaciones:**
  - `estudianteId`: debe existir
- **Respuesta:** Lista de inscripciones.

#### GET `/api/inscripciones/materia/{materiaId}`
- **Descripción:** Lista todas las inscripciones de una materia.
- **Validaciones:**
  - `materiaId`: debe existir
- **Respuesta:** Lista de inscripciones.

#### GET `/api/inscripciones/estudiante/{estudianteId}/activas`
- **Descripción:** Lista inscripciones activas de un estudiante.
- **Validaciones:**
  - `estudianteId`: debe existir
- **Respuesta:** Lista de inscripciones activas.
