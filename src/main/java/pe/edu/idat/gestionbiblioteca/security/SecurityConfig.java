package pe.edu.idat.gestionbiblioteca.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.edu.idat.gestionbiblioteca.service.CaptchaService;
import pe.edu.idat.gestionbiblioteca.service.DetalleUsuarioService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final DetalleUsuarioService detalleUsuarioService;
    private final PasswordEncoder passwordEncoder;
    private final CaptchaService captchaService;

    @Bean
    public SecurityFilterChain config(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers("/auth/login",
                        "/auth/registrar",
                        "/auth/guardarUsuario",
                        "/resources/**",
                        "/static/**",
                        "/styles/**",
                        "/scripts/**").permitAll()
                        .requestMatchers("/categoria/**", "/libro/**", "/prestamo/**")
                        .hasAnyRole("ADMIN", "BIBLIOTECARIO")
                .requestMatchers("/misprestamos").hasRole("LECTOR")
                .requestMatchers("/usuario/**").hasRole("ADMIN")
                .requestMatchers("/backoffice", "/backoffice/**")
                        .hasAnyRole("ADMIN", "BIBLIOTECARIO")
                .anyRequest().authenticated()
        ).formLogin((form) ->
                form.loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/auth/login-success", true)
                        .failureHandler(this::loginFallido)
                        .usernameParameter("nomusuario")
                        .passwordParameter("password")
                        .permitAll()
        ).logout(logout ->
                logout.logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
        ).exceptionHandling(exception ->
                exception.accessDeniedPage("/auth/denegado")
        ).authenticationProvider(authenticationProvider())
        .addFilterBefore(new CaptchaLoginFilter(captchaService),
                UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider
                = new DaoAuthenticationProvider(detalleUsuarioService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    // Redirige al login conservando el usuario ingresado para no pedirlo de nuevo.
    private void loginFallido(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        String nomusuario = request.getParameter("nomusuario");
        String destino = "/auth/login?error=true";
        if (nomusuario != null && !nomusuario.isBlank()) {
            destino += "&nomusuario=" + URLEncoder.encode(nomusuario, StandardCharsets.UTF_8);
        }
        response.sendRedirect(request.getContextPath() + destino);
    }
}
