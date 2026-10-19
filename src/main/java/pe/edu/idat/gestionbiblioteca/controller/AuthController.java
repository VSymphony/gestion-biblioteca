package pe.edu.idat.gestionbiblioteca.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.idat.gestionbiblioteca.dto.RegistroUsuarioDto;
import pe.edu.idat.gestionbiblioteca.dto.UsuarioSeguridadDto;
import pe.edu.idat.gestionbiblioteca.model.Usuario;
import pe.edu.idat.gestionbiblioteca.service.CaptchaService;
import pe.edu.idat.gestionbiblioteca.service.UsuarioService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UsuarioService usuarioService;
    private final CaptchaService captchaService;

    @Value("${recaptcha.site.key}")
    private String recaptchaSiteKey;

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
        return "auth/frmLogin";
    }

    @GetMapping("/registrar")
    public String registrar(Model model){
        model.addAttribute("usuario", new RegistroUsuarioDto());
        model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
        return "auth/frmRegistroUsuario";
    }

    @GetMapping("/login-success")
    public String loginSuccess(){
        return "redirect:/auth/home";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UsuarioSeguridadDto dto, Model model){
        if(dto == null){
            return "redirect:/auth/login";
        }
        String nombres = dto.getNombres();
        if (nombres == null || nombres.isBlank()) {
            nombres = dto.getUsername();
        }
        model.addAttribute("nombres", nombres);
        model.addAttribute("nombre", dto.getUsername());
        model.addAttribute("email", dto.getEmail());
        return "auth/home";
    }

    @RequestMapping("/denegado")
    public String denegado(){
        return "auth/denegado";
    }

    @PostMapping("/guardarUsuario")
    public String guardarUsuario(@Valid @ModelAttribute("usuario") RegistroUsuarioDto dto,
                                 BindingResult bindingResult,
                                 @RequestParam(name = "g-recaptcha-response", required = false) String captchaResponse,
                                 Model model){
        if(!captchaService.validar(captchaResponse)){
            model.addAttribute("captchaError", "Captcha inválido. Inténtalo de nuevo.");
            model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
            dto.setPassword(null);
            return "auth/frmRegistroUsuario";
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
            dto.setPassword(null);
            return "auth/frmRegistroUsuario";
        }
        Usuario usuario = new Usuario();
        usuario.setNombres(dto.getNombres().trim());
        usuario.setApellidos(dto.getApellidos().trim());
        usuario.setNomusuario(dto.getNomusuario().trim());
        usuario.setEmail(dto.getEmail().trim());
        usuario.setPassword(dto.getPassword());
        usuarioService.saveUser(usuario);
        return "redirect:/auth/login?registrado";
    }
}
