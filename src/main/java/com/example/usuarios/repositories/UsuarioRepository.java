package com.example.usuarios.repositories;

import com.example.usuarios.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Validaciones de unicidad para evitar el error 409
    boolean existsByNombreUsuario(String nombreUsuario);
    boolean existsByEmail(String email);
}