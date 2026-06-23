package com.polideportivo.polideportivo.config;

import com.polideportivo.polideportivo.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios", "/usuarios/", "/usuarios/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/disciplinas/**").hasAnyRole("ADMINISTRADOR", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/espacios/**").hasAnyRole("ADMINISTRADOR", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/equipamientos/**").hasAnyRole("ADMINISTRADOR", "CLIENTE")

                        .requestMatchers("/usuarios/me/**").hasAnyRole("ADMINISTRADOR", "CLIENTE")
                        .requestMatchers("/reservas/mis-reservas/**").hasRole("CLIENTE")
                        .requestMatchers("/pagos/mis-pagos/**").hasRole("CLIENTE")
                        .requestMatchers("/reserva-equipamientos/mis-**").hasRole("CLIENTE")

                        .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/disciplinas/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/espacios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/equipamientos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/reservas/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/pagos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/reserva-equipamientos/**").hasRole("ADMINISTRADOR")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}