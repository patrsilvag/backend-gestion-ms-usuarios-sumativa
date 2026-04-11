package com.example.usuarios.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.example.usuarios.dto.LoginRequest;
import com.example.usuarios.dto.UserResponse;
import com.example.usuarios.exceptions.AccesoDenegadoException;
import com.example.usuarios.exceptions.DuplicateResourceException;
import com.example.usuarios.exceptions.ResourceNotFoundException;
import com.example.usuarios.models.Usuario;
import com.example.usuarios.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@SuppressWarnings({"null", "all"})
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

    public UserResponse guardar(@Valid Usuario usuario, String rolSolicitante) {
    
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new DuplicateResourceException(
                    "El correo '" + usuario.getEmail() + "' ya existe.");
        }
        return convertToResponse(usuarioRepository.save(usuario));
    }

    public UserResponse actualizar(@NonNull Long id, @Valid Usuario datos, String rol) {
        
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID " + id + " no existe."));

        existente.setNombreUsuario(datos.getNombreUsuario());
        existente.setEmail(datos.getEmail());
        existente.setPassword(datos.getPassword());
        existente.setRol(datos.getRol());

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
        return new UserResponse(u.getId(), u.getNombreUsuario(), u.getEmail(), u.getRol().name());
    }
}
