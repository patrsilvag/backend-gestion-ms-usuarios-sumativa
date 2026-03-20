package com.example.usuarios.controllers;

import com.example.usuarios.models.Usuario;
import com.example.usuarios.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para ver quiénes están registrados
    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.listarTodos();
    }

    // Endpoint para crear nuevos usuarios
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(
            @Valid @RequestBody Usuario usuario,
            @RequestParam String rol) {
        return new ResponseEntity<>(usuarioService.guardar(usuario, rol), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario,
            @RequestParam String rol) {
        return ResponseEntity.ok(usuarioService.actualizar(id, usuario, rol));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(
            @PathVariable Long id,
            @RequestParam String rol) {
        usuarioService.eliminar(id, rol);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }

}