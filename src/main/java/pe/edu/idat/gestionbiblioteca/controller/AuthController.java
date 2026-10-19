package pe.edu.idat.gestionbiblioteca.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.idat.gestionbiblioteca.dto.UsuarioSeguridadDto;
import pe.edu.idat.gestionbiblioteca.model.Usuario;
import pe.edu.idat.gestionbiblioteca.service.UsuarioService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String login(){
        return "auth/frmLogin";
    }

    @GetMapping("/registrar")
    public String registrar(Model model){
        model.addAttribute("usuario", new Usuario());
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

    @GetMapping("/denegado")
    public String denegado(){
        return "auth/denegado";
    }

    @PostMapping("/guardarUsuario")
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario){
        usuarioService.saveUser(usuario);
        return "redirect:/auth/login";
    }
}
