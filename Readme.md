# 👤 Sistema de Gestión de Pedidos - Microservicio de Usuarios

Este proyecto es el segundo componente de grado ingeniería para la asignatura de Desarrollo de Software en **Duoc UC**. Se trata de un microservicio robusto para la **gestión centralizada de identidades**, integrado con **Oracle Cloud Infrastructure (OCI)** y diseñado bajo el patrón de diseño de Microservicios Independientes.

## 🚀 Especificaciones Técnicas
* **Lenguaje:** Java 21 (LTS)
* **Framework:** Spring Boot 3.5.12
* **Base de Datos:** Oracle Autonomous Database (Cloud)
* **Gestión de Dependencias:** Maven
* **Puerto de Servicio:** 8082

## 🛠️ Características Principales

### 1. Persistencia y Modelado de Datos
El microservicio gestiona la tabla `USUARIOS` en la nube de Oracle, utilizando una arquitectura de tres capas (Controller, Service, Repository). La columna primaria está estandarizada como `ID_USUARIO` para cumplir con las mejores prácticas de modelado de datos.

### 2. Privacidad por Diseño (Data Transfer Objects)
Se implementaron patrones **DTO** para separar la capa de persistencia de la capa de presentación:
* **`LoginRequest`**: Objeto especializado para recibir credenciales de forma segura.
* **`UserResponse`**: Objeto de salida que **excluye la contraseña**, garantizando que los datos sensibles nunca abandonen el perímetro del microservicio en las respuestas JSON.

### 3. Validaciones de Integridad y Roles
* **Seguridad de Credenciales**: Validación mediante `@Size` para asegurar claves de al menos 8 caracteres.
* **Normalización de Roles**: Uso del Enum **`Rol.java`** (`ADMIN`, `CLIENTE`) con la anotación `@Enumerated(EnumType.STRING)` para evitar inconsistencias en la base de datos.

## 📑 API Endpoints y Códigos de Respuesta

| Método | Endpoint | Descripción | Código Éxito |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/usuarios/login` | **Autenticación**: Inicia sesión con email y password | 200 OK |
| **GET** | `/api/usuarios` | Lista todos los usuarios (Formato `UserResponse`) | 200 OK |
| **GET** | `/api/usuarios/{id}` | Busca un usuario específico por su ID único | 200 OK |
| **POST** | `/api/usuarios` | Registra un nuevo usuario (Requiere `?rol=ADMIN`) | 201 Created |
| **PUT** | `/api/usuarios/{id}` | Actualiza datos de un usuario (Requiere `?rol=ADMIN`) | 200 OK |
| **DELETE**| `/api/usuarios/{id}`| Elimina físicamente un usuario (Requiere `?rol=ADMIN`)| 204 No Content |

### Manejo de Errores Estandarizado
Se utiliza un `GlobalExceptionHandler` para garantizar respuestas consistentes:
* **400 Bad Request**: Fallo en validaciones de formato (email inválido o password corta).
* **403 Forbidden**: Intento de gestión administrativa con rol de `CLIENTE`.
* **404 Not Found**: El recurso solicitado no existe en Oracle Cloud.
* **409 Conflict**: El correo electrónico o nombre de usuario ya se encuentran registrados.

## 🧪 Pruebas con Postman
La colección adjunta para el puerto **8082** permite validar el flujo completo de identidad:
1. **Flujo de Acceso**: Prueba del endpoint de login con respuesta segura (sin contraseña).
2. **Ciclo CRUD**: Creación de los **3 usuarios mínimos** exigidos por la pauta.
3. **Validación de Identidad**: Búsqueda individual por ID para verificar la integridad de la información en la nube.
4. **Seguridad RBAC**: Intento de eliminación fallido para validar el control de acceso basado en roles[cite: 25].

---
