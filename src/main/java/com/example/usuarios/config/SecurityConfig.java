package com.example.usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Definimos la constante una sola vez
    private static final String USUARIOS_PATH_WILDCARD = "/api/usuarios/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // 1. Permitir el pre-flight (IMPORTANTE para PUT)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Permitir el PUT específicamente para anónimos
                        .requestMatchers(HttpMethod.PUT,USUARIOS_PATH_WILDCARD).permitAll()

                        // 3. El resto de las rutas que ya tenías
                        .requestMatchers("/api/usuarios/login", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, USUARIOS_PATH_WILDCARD).permitAll()
                        .requestMatchers(HttpMethod.POST, USUARIOS_PATH_WILDCARD).permitAll()

                        .anyRequest().authenticated())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // 3. BEAN DE CONFIGURACIÓN DE CORS (Indispensable para Docker)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost", "http://localhost:4200"));
        // 👈 3. Asegúrate de que PUT esté en esta lista
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
