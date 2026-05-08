package com.example.usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // <--- ESTE ES EL IMPORT QUE
                                                                       // FALTABA
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Definimos la constante al inicio de la clase para evitar duplicados en SonarQube
    private static final String USUARIOS_PATH_WILDCARD = "/api/usuarios/**";
    private static final String USUARIOS_BASE_PATH = "/api/usuarios";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Deshabilitado para APIs Stateless (JWT)
                .cors(Customizer.withDefaults()).authorizeHttpRequests(auth -> auth
                        // Uso de constantes para mayor mantenibilidad
                        .requestMatchers(HttpMethod.GET, USUARIOS_PATH_WILDCARD).permitAll()
                        .requestMatchers(HttpMethod.POST, USUARIOS_BASE_PATH).permitAll()
                        .requestMatchers("/api/usuarios/login").permitAll()
                        .requestMatchers(HttpMethod.PUT, USUARIOS_PATH_WILDCARD).permitAll()
                        .requestMatchers(HttpMethod.DELETE, USUARIOS_PATH_WILDCARD).authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(session ->
                // Define que no se crearán sesiones en el servidor (Arquitectura Stateless)
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
