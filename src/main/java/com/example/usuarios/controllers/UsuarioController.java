package com.example.usuarios.controllers;

import com.example.usuarios.dto.*;
import com.example.usuarios.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// ✅ Se mantienen los orígenes permitidos para Docker y local
@CrossOrigin(origins = {"http://mi-app-docker", "http://localhost", "http://localhost:4200"},
        allowedHeaders = "*",
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) { // 🚨 DEBE tener
                                                                               // @RequestBody
     System.out.println("------> ¡LA PETICIÓN LLEGÓ AL CONTROLLER! <------");
        
        return ResponseEntity.ok(usuarioService.login(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping({"", "/"})
    public List<UserResponse> obtenerUsuarios() {
        return usuarioService.listarTodos();
    }

    // 🛠️ CORRECCIÓN: Se añade el soporte para la barra final en el POST
    @PostMapping({"", "/"})
    public ResponseEntity<UserResponse> crearUsuario(@Valid @RequestBody UserRequest request,
            @RequestParam(required = false) String rol) { // 👈 Cambiado a opcional

                // Agrega este print para ver el hit en Docker
        System.out.println("------> ¡REGISTRO LLEGÓ AL CONTROLLER! <------");
        // Si 'rol' llega nulo por URL, el Service usará request.getRol()
        return new ResponseEntity<>(usuarioService.guardar(request, rol), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> actualizarUsuario(@PathVariable Long id,
            @Valid @RequestBody UserRequest request, @RequestParam String rol) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request, rol));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id, @RequestParam String rol) {
        usuarioService.eliminar(id, rol);
        return ResponseEntity.noContent().build();
    }
}
