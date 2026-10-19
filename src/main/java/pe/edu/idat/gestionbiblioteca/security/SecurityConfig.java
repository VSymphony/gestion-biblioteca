package pe.edu.idat.gestionbiblioteca.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pe.edu.idat.gestionbiblioteca.service.DetalleUsuarioService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final DetalleUsuarioService detalleUsuarioService;
    private final PasswordEncoder passwordEncoder;

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
                .requestMatchers("/backoffice", "/backoffice/**")
                        .hasAnyRole("ADMIN", "BIBLIOTECARIO")
                .anyRequest().authenticated()
        ).formLogin((form) ->
                form.loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/auth/login-success", true)
                        .failureUrl("/auth/login?error=true")
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
        ).authenticationProvider(authenticationProvider());
        return http.build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider
                = new DaoAuthenticationProvider(detalleUsuarioService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }
}
