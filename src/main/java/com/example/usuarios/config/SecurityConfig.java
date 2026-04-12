package com.example.usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Necesario para que Docker y Angular puedan hacer POST
                .cors(Customizer.withDefaults()) // Activa la configuración de CORS que ya tienes
                .authorizeHttpRequests(auth -> auth
                       
                        // 1. PRIMERO: Permitir explícitamente el GET a la lista
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").permitAll()

                        // PERMITIR registro de nuevos usuarios (POST a /api/usuarios)
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").permitAll()
                        // PERMITIR el login
                        .requestMatchers("/api/usuarios/login").permitAll()
                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults()); // Permite autenticación básica por si la
                                                       // necesitas

        return http.build();
    }
}
