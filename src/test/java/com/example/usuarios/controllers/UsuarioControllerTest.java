package com.example.usuarios.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.usuarios.exceptions.*;
import com.example.usuarios.services.UsuarioService;
// Estos imports ya no se marcarán como 'unused' porque se usan en el stubbing
import com.example.usuarios.dto.LoginRequest;
import com.example.usuarios.dto.UserResponse;
import com.example.usuarios.dto.UserRequest;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("null")
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        // Inyección por constructor para Rating A en Reliability
        usuarioController = new UsuarioController(usuarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @Test
    void testLogin_Exitoso() throws Exception {
        // 1. Declaramos los objetos
        LoginRequest login = new LoginRequest("test@correo.cl", "pass1234");
        UserResponse response = new UserResponse(1L, "profe", "test@correo.cl", "ADMIN");

        // 2. Usamos 'login' en el stubbing. Esto elimina la advertencia de "not used"
        when(usuarioService.login(login)).thenReturn(response);

        // 3. Ejecutamos la petición
        String json = "{\"email\":\"test@correo.cl\",\"password\":\"pass1234\"}";
        mockMvc.perform(
                post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testListarTodos() throws Exception {
        // Uso explícito de UserResponse
        UserResponse user = new UserResponse(1L, "test", "test@test.cl", "CLIENTE");
        when(usuarioService.listarTodos()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/api/usuarios")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testObtenerPorId_Exitoso() throws Exception {
        UserResponse response = new UserResponse(1L, "profe", "test@correo.cl", "ADMIN");
        when(usuarioService.buscarPorId(anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/usuarios/1")).andExpect(status().isOk());
    }

    @Test
    void testErrorNoEncontrado() throws Exception {
        when(usuarioService.buscarPorId(anyLong()))
                .thenThrow(new ResourceNotFoundException("No existe"));

        mockMvc.perform(get("/api/usuarios/99")).andExpect(status().isNotFound()); // Verifica el
                                                                                   // 404 del
                                                                                   // Handler
    }

    @Test
    void testCrearUsuario_Exitoso() throws Exception {
        // Creamos el objeto para usar el import y la variable
        UserRequest request = new UserRequest("profe_test", "test@correo.cl", "clave1234", "ADMIN");
        UserResponse response = new UserResponse(1L, "profe_test", "test@correo.cl", "ADMIN");

        // Al usar 'request' aquí, el IDE dejará de marcarlo como "unused"
        when(usuarioService.guardar(eq(request), anyString())).thenReturn(response);

        String json =
                "{\"nombreUsuario\":\"profe_test\",\"email\":\"test@correo.cl\",\"password\":\"clave1234\",\"rol\":\"ADMIN\"}";

        mockMvc.perform(post("/api/usuarios").param("rol", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void testActualizarUsuario_Exitoso() throws Exception {
        UserResponse response = new UserResponse(1L, "editado", "test@correo.cl", "CLIENTE");
        when(usuarioService.actualizar(anyLong(), any(UserRequest.class), anyString()))
                .thenReturn(response);

        String json =
                "{\"nombreUsuario\":\"editado\",\"email\":\"test@correo.cl\",\"password\":\"nuevaClave123\",\"rol\":\"CLIENTE\"}";

        mockMvc.perform(put("/api/usuarios/1").param("rol", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
    }

    @Test
    void testEliminar_Exitoso() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1").param("rol", "ADMIN"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testErrorDuplicado() throws Exception {
        // Dispara DuplicateResourceException para subir cobertura del Handler
        when(usuarioService.guardar(any(), anyString()))
                .thenThrow(new DuplicateResourceException("Email duplicado"));

        String json =
                "{\"nombreUsuario\":\"profe\",\"email\":\"test@test.cl\",\"password\":\"12345678\",\"rol\":\"ADMIN\"}";
        mockMvc.perform(post("/api/usuarios").param("rol", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict()); // Status 409
    }

    @Test
    void testErrorAccesoDenegado() throws Exception {
        // Dispara AccesoDenegadoException para cubrir la clase al 100%
        doThrow(new AccesoDenegadoException("Denegado")).when(usuarioService).eliminar(anyLong(),
                anyString());

        mockMvc.perform(delete("/api/usuarios/1").param("rol", "USER"))
                .andExpect(status().isForbidden()); // Status 403
    }
}
