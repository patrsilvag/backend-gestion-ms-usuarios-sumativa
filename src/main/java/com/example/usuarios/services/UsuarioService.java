package com.example.usuarios.services;

import com.example.usuarios.models.Usuario;
import com.example.usuarios.repositories.UsuarioRepository;
import com.example.usuarios.exceptions.AccesoDenegadoException;
import com.example.usuarios.exceptions.DuplicateResourceException;
import com.example.usuarios.exceptions.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        log.info("Service: Listando todos los usuarios");
        return usuarioRepository.findAll();
    }

    @SuppressWarnings("null")
    public Usuario guardar(Usuario usuario, String rolSolicitante) {
        // 1. Seguridad: Solo un ADMIN puede crear usuarios
        validarAdmin(rolSolicitante);

        // 2. Validación de correo duplicado
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            log.warn("Service: Intento de registro con email duplicado: {}", usuario.getEmail());
            throw new DuplicateResourceException("El correo '" + usuario.getEmail() + "' ya existe.");
        }

        log.info("Service: Registrando usuario '{}'", usuario.getNombreUsuario());
        return usuarioRepository.save(usuario);
    }

    private void validarAdmin(String rol) {
        if (!"ADMIN".equalsIgnoreCase(rol)) {
            throw new AccesoDenegadoException("No tienes permisos para gestionar usuarios.");
        }
    }


    public Usuario actualizar(Long id, Usuario datosActualizados, String rolSolicitante) {
        validarAdmin(rolSolicitante); // Solo ADMIN actualiza
        
        Usuario usuarioExistente = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Validar si el nuevo email ya lo tiene otro usuario
        if (!usuarioExistente.getEmail().equals(datosActualizados.getEmail()) && 
            usuarioRepository.existsByEmail(datosActualizados.getEmail())) {
            throw new DuplicateResourceException("El nuevo correo ya está en uso.");
        }

        usuarioExistente.setNombreUsuario(datosActualizados.getNombreUsuario());
        usuarioExistente.setEmail(datosActualizados.getEmail());
        usuarioExistente.setPassword(datosActualizados.getPassword());
        usuarioExistente.setRol(datosActualizados.getRol());

        log.info("Service: Actualizando usuario ID {}", id);
        return usuarioRepository.save(usuarioExistente);
    }

    public void eliminar(Long id, String rolSolicitante) {
        validarAdmin(rolSolicitante); // Solo ADMIN elimina
        
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: ID " + id + " no existe.");
        }
        
        log.info("Service: Eliminando usuario ID {}", id);
        usuarioRepository.deleteById(id);
    }
}