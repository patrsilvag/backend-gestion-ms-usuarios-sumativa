package com.example.usuarios;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public SecurityConfig() {
        System.out.println("⚠️ CONFIGURACIÓN DE SEGURIDAD CARGADA EXITOSAMENTE ⚠️");
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Esto crea un gestor de usuarios vacío para que Spring deje de generar la clave aleatoria
        return new InMemoryUserDetailsManager();
    }    

    private static final String USUARIOS_PATH_WILDCARD = "/api/usuarios/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll().requestMatchers(HttpMethod.PUT, USUARIOS_PATH_WILDCARD)
                        .permitAll().requestMatchers("/api/usuarios/login", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, USUARIOS_PATH_WILDCARD).permitAll()
                        .requestMatchers(HttpMethod.POST, USUARIOS_PATH_WILDCARD).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // ✅ Se añade el dominio del entorno Docker
        configuration.setAllowedOrigins(
                Arrays.asList("http://mi-app-docker", "http://localhost", "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
