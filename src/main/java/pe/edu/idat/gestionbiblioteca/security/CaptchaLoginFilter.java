package pe.edu.idat.gestionbiblioteca.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.edu.idat.gestionbiblioteca.service.CaptchaService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// Corre antes del filtro de login: sin captcha valido no se llega a autenticar.
@RequiredArgsConstructor
public class CaptchaLoginFilter extends OncePerRequestFilter {

    private final CaptchaService captchaService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equalsIgnoreCase(request.getMethod())
                && "/auth/login".equals(request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String captchaResponse = request.getParameter("g-recaptcha-response");
        if (!captchaService.validar(captchaResponse)) {
            String destino = "/auth/login?captchaError=true";
            String nomusuario = request.getParameter("nomusuario");
            if (nomusuario != null && !nomusuario.isBlank()) {
                destino += "&nomusuario=" + URLEncoder.encode(nomusuario, StandardCharsets.UTF_8);
            }
            response.sendRedirect(request.getContextPath() + destino);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
