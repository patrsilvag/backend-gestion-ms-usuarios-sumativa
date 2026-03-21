package com.example.usuarios.dto;

// UserResponse.java
public record UserResponse(
    Long id,
    String nombreUsuario,
    String email,
    String rol
) {
}