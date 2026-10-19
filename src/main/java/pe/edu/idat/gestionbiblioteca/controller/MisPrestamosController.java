package pe.edu.idat.gestionbiblioteca.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.idat.gestionbiblioteca.dto.UsuarioSeguridadDto;
import pe.edu.idat.gestionbiblioteca.model.Usuario;
import pe.edu.idat.gestionbiblioteca.repository.PrestamoRepository;
import pe.edu.idat.gestionbiblioteca.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MisPrestamosController {

    private final UsuarioRepository usuarioRepository;
    private final PrestamoRepository prestamoRepository;

    @GetMapping("/misprestamos")
    public String misPrestamos(@AuthenticationPrincipal UsuarioSeguridadDto dto, Model model) {
        Usuario usuario = usuarioRepository.findByNomusuario(dto.getUsername());
        model.addAttribute("prestamos", usuario == null
                ? List.of()
                : prestamoRepository.findByUsuarioOrderByIdprestamoDesc(usuario));
        model.addAttribute("hoy", LocalDate.now());
        return "misPrestamos";
    }
}
