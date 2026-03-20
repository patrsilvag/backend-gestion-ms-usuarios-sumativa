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
El microservicio gestiona la tabla `USUARIOS` en la nube de Oracle. Utiliza una arquitectura de tres capas (Controller, Service, Repository) para garantizar que la lógica de negocio esté aislada de la persistencia.

### 2. Validaciones de Integridad (Bean Validation)
* **Seguridad de Credenciales:** La contraseña cuenta con una restricción mínima de 8 caracteres mediante `@Size` para fomentar prácticas de seguridad básicas.
* **Unicidad de Identidad:** Implementación de lógica `existsByEmail` y `existsByNombreUsuario` para prevenir cuentas duplicadas, arrojando errores controlados de tipo **409 Conflict**.
* **Normalización de Roles:** Uso de un **Enum (`Rol.java`)** para asegurar que solo se acepten los valores `ADMIN` o `CLIENTE`, evitando errores de digitación en la base de datos.

### 3. Seguridad y Control de Acceso (RBAC)
Se implementa un control de acceso basado en roles (Role-Based Access Control) mediante parámetros de consulta en el `Service`:
* **ADMIN:** Único rol autorizado para realizar operaciones de escritura (`POST`), edición (`PUT`) y eliminación (`DELETE`).
* **CLIENTE:** Rol con permisos restringidos únicamente a la consulta de información.

## 📑 API Endpoints y Códigos de Respuesta

| Método | Endpoint | Descripción | Código Éxito |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/usuarios` | Lista todos los usuarios registrados | 200 OK |
| **POST** | `/api/usuarios` | Registra un nuevo usuario (Requiere `?rol=ADMIN`) | 201 Created |
| **PUT** | `/api/usuarios/{id}` | Actualiza datos de un usuario (Requiere `?rol=ADMIN`) | 200 OK |
| **DELETE**| `/api/usuarios/{id}`| Elimina físicamente un usuario (Requiere `?rol=ADMIN`)| 204 No Content |

### Manejo de Errores Estandarizado
Al igual que el microservicio de productos, este sistema utiliza un `GlobalExceptionHandler` para garantizar respuestas consistentes:
* **400 Bad Request:** Formato de email inválido o contraseña demasiado corta.
* **403 Forbidden:** Un usuario con rol `CLIENTE` intentando crear o borrar otros usuarios.
* **404 Not Found:** Intento de actualización o borrado de un ID que no existe en Oracle Cloud.
* **409 Conflict:** El correo electrónico ya se encuentra registrado en el sistema.

## 🧪 Pruebas con Postman
Se adjunta una colección de Postman para el puerto **8082** que incluye:
1. **Carga Inicial:** Creación de 3 usuarios (Admin, Cliente, Soporte).
2. **Ciclo de Vida:** Pruebas de actualización de perfil y eliminación con respuesta **204 No Content**.
3. **Escenarios de Falla:** Validación de denegación de acceso y detección de emails duplicados.

---
