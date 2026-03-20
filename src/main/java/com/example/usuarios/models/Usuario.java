package com.example.usuarios.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull; 
import lombok.Data;

/**
 * Entidad Usuario para el Microservicio de Usuarios (8082).
 * Implementa Bean Validation para asegurar la integridad en Oracle Cloud.
 */
@Data
@Entity
@Table(name = "USUARIOS")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 20, message = "El nombre debe tener entre 4 y 20 caracteres")
    private String nombreUsuario;

    @Email(message = "Debe ser un formato de correo válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La clave debe tener al menos 8 caracteres")
    private String password;
  
    @Enumerated(EnumType.STRING) // para que en Oracle se guarde como texto y no como número
    @NotNull(message = "El rol es obligatorio")
    private Rol rol;
}