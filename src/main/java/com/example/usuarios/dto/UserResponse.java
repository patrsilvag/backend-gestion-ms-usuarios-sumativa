package com.example.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // <-- CRÍTICO: Sin esto, listarTodos siempre dará error 500
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String nombreUsuario;
    private String email;
    private String rol;
}
