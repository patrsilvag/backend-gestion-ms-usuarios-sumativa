package com.example.usuarios.controllers;

import com.example.usuarios.models.Usuario;
import com.example.usuarios.dto.*;
import com.example.usuarios.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }
    
    @GetMapping
    public List<UserResponse> obtenerUsuarios() {
        return usuarioService.listarTodos();
    }

    @PostMapping
    public ResponseEntity<UserResponse> crearUsuario(
            @Valid @RequestBody Usuario usuario,
            @RequestParam String rol) {
        return new ResponseEntity<>(usuarioService.guardar(usuario, rol), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario,
            @RequestParam String rol) {
        return ResponseEntity.ok(usuarioService.actualizar(id, usuario, rol));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id, @RequestParam String rol) {
        usuarioService.eliminar(id, rol);
        return ResponseEntity.noContent().build();
    }
}