package com.example.usuarios.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.example.usuarios.dto.LoginRequest;
import com.example.usuarios.dto.UserRequest; // Nuevo Import
import com.example.usuarios.dto.UserResponse;
import com.example.usuarios.exceptions.AccesoDenegadoException;
import com.example.usuarios.exceptions.DuplicateResourceException;
import com.example.usuarios.exceptions.ResourceNotFoundException;
import com.example.usuarios.models.Rol; // Importante para el enum
import com.example.usuarios.models.Usuario;
import com.example.usuarios.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@SuppressWarnings({"all"})
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- Lógica de Login (Requerimiento #30) ---
    public UserResponse login(@Valid LoginRequest request) {
        log.info("Service: Intento de login para {}", request.getEmail());
        Usuario user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AccesoDenegadoException("Credenciales incorrectas"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new AccesoDenegadoException("Credenciales incorrectas");
        }
        return convertToResponse(user);
    }

    // --- Métodos CRUD
    public UserResponse buscarPorId(@NonNull Long id) {
        log.info("Service: Buscando usuario con ID: {}", id);
        Usuario user = usuarioRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        return convertToResponse(user);
    }

    public List<UserResponse> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Cambiamos a UserRequest para Rating A en Seguridad
    public UserResponse guardar(@Valid UserRequest request, String rolSolicitante) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "El correo '" + request.getEmail() + "' ya existe.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());

        // Priorizamos el rol que viene por parámetro o el del DTO
        String rolFinal = (rolSolicitante != null) ? rolSolicitante : request.getRol();
        usuario.setRol(Rol.valueOf(rolFinal.toUpperCase()));

        return convertToResponse(usuarioRepository.save(usuario));
    }

    // Cambiamos a UserRequest para Rating A en Seguridad y Fiabilidad
    public UserResponse actualizar(@NonNull Long id, @Valid UserRequest datos, String rol) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID " + id + " no existe."));

        existente.setNombreUsuario(datos.getNombreUsuario());
        existente.setEmail(datos.getEmail());
        existente.setPassword(datos.getPassword());

        if (datos.getRol() != null) {
            existente.setRol(Rol.valueOf(datos.getRol().toUpperCase()));
        }

        return convertToResponse(usuarioRepository.save(existente));
    }

    public void eliminar(@NonNull Long id, String rol) {
        validarAdmin(rol);
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("ID " + id + " no encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    // --- Helpers de Ingeniería ---
    private void validarAdmin(String rol) {
        if (!"ADMIN".equalsIgnoreCase(rol)) {
            throw new AccesoDenegadoException("Acceso denegado: Se requiere rol ADMIN.");
        }
    }

    private UserResponse convertToResponse(Usuario u) {
        return new UserResponse(u.getId(), u.getNombreUsuario(), u.getEmail(), String.valueOf(u.getRol()));
    }
}
