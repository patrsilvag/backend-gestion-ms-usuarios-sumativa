package com.example.usuarios.controllers;

import com.example.usuarios.dto.*;
import com.example.usuarios.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*",
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // 1. Cambio a 'final' para inmutabilidad y Reliability Rating A
    private final UsuarioService usuarioService;

    // 2. Inyección por constructor (Elimina el @Autowired de campo)
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

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
    public ResponseEntity<UserResponse> crearUsuario(@Valid @RequestBody UserRequest request, // 3.
                                                                                              // Cambio
                                                                                              // a
                                                                                              // DTO
                                                                                              // para
                                                                                              // Security
                                                                                              // Rating
                                                                                              // A
            @RequestParam String rol) {
        return new ResponseEntity<>(usuarioService.guardar(request, rol), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> actualizarUsuario(@PathVariable Long id,
            @Valid @RequestBody UserRequest request, // 3. Cambio a DTO para Security Rating A
            @RequestParam String rol) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request, rol));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id, @RequestParam String rol) {
        usuarioService.eliminar(id, rol);
        return ResponseEntity.noContent().build();
    }
}
