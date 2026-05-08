package com.example.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 20)
    private String nombreUsuario;

    @Email(message = "Formato de email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8)
    private String password;

    private String rol;
}
