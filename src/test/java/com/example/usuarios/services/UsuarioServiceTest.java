package com.example.usuarios.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.usuarios.dto.LoginRequest;
import com.example.usuarios.dto.UserRequest; // <--- NUEVO IMPORT
import com.example.usuarios.dto.UserResponse;
import com.example.usuarios.exceptions.AccesoDenegadoException;
import com.example.usuarios.exceptions.DuplicateResourceException;
import com.example.usuarios.exceptions.ResourceNotFoundException;
import com.example.usuarios.models.Rol;
import com.example.usuarios.models.Usuario;
import com.example.usuarios.repositories.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("all")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private LoginRequest loginRequest;
    private UserRequest userRequest; // <--- DTO para las pruebas

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreUsuario("profesor_test");
        usuario.setEmail("test@correo.cl");
        usuario.setPassword("password123");
        usuario.setRol(Rol.CLIENTE);

        loginRequest = new LoginRequest("test@correo.cl", "password123");

        // Inicializamos el DTO de entrada
        userRequest = new UserRequest();
        userRequest.setNombreUsuario("profesor_test");
        userRequest.setEmail("test@correo.cl");
        userRequest.setPassword("password123");
        userRequest.setRol("CLIENTE");
    }

    @Test
    void login_Exitoso() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        UserResponse response = usuarioService.login(loginRequest);
        assertNotNull(response);
        assertEquals("test@correo.cl", response.getEmail());
    }

    @Test
    void buscarPorId_Exitoso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        UserResponse response = usuarioService.buscarPorId(1L);
        assertNotNull(response);
        assertEquals("test@correo.cl", response.getEmail());
    }

    @Test
    void guardar_EmailDuplicado_LanzaDuplicateResource() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);
        // Ajustado para pasar 'userRequest' en lugar de 'usuario'
        assertThrows(DuplicateResourceException.class,
                () -> usuarioService.guardar(userRequest, "ADMIN"));
    }

    @Test
    void guardar_Exitoso() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Ajustado para pasar 'userRequest'
        UserResponse response = usuarioService.guardar(userRequest, "ADMIN");
        assertNotNull(response);
    }

    @Test
    void actualizar_Exitoso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Ajustado para pasar 'userRequest'
        UserResponse response = usuarioService.actualizar(1L, userRequest, "ADMIN");
        assertNotNull(response);
    }

    @Test
    void eliminar_ComoAdmin_Exitoso() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> usuarioService.eliminar(1L, "ADMIN"));
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void listarTodos_RetornaLista() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));
        List<UserResponse> lista = usuarioService.listarTodos();
        assertFalse(lista.isEmpty());
    }
}
