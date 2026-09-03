package cl.duoc.pedidos360.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Configuración TEMPORAL para probar el CRUD localmente sin autenticación.
     * Será reemplazada por seguridad JWT con Microsoft Entra ID.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitado temporalmente para permitir POST, PUT y DELETE en pruebas locales.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/**", "/actuator/health").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }
}
