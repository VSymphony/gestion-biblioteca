package pe.edu.idat.gestionbiblioteca.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.idat.gestionbiblioteca.dto.UsuarioSeguridadDto;
import pe.edu.idat.gestionbiblioteca.exception.PrestamoInvalidoException;
import pe.edu.idat.gestionbiblioteca.model.Libro;
import pe.edu.idat.gestionbiblioteca.model.Prestamo;
import pe.edu.idat.gestionbiblioteca.model.Usuario;
import pe.edu.idat.gestionbiblioteca.repository.LibroRepository;
import pe.edu.idat.gestionbiblioteca.repository.UsuarioRepository;
import pe.edu.idat.gestionbiblioteca.service.PrestamoService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CatalogoController {

    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestamoService prestamoService;

    @GetMapping("/")
    public String raiz() {
        return "redirect:/auth/home";
    }

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(required = false) String q, Model model) {
        List<Libro> libros;
        if (q == null || q.isBlank()) {
            libros = libroRepository.findAll(Sort.by("titulo"));
        } else {
            String texto = q.trim();
            libros = libroRepository
                    .findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrderByTitulo(
                            texto, texto);
        }
        model.addAttribute("libros", libros);
        model.addAttribute("q", q);
        return "catalogo";
    }

    // Autoservicio del lector: solicita el libro y el prestamo queda registrado.
    @PreAuthorize("hasRole('LECTOR')")
    @PostMapping("/catalogo/solicitar")
    public String solicitar(@RequestParam(required = false) Integer idlibro,
                            @AuthenticationPrincipal UsuarioSeguridadDto dto,
                            RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioRepository.findByNomusuario(dto.getUsername());
        Libro libro = idlibro == null ? null : libroRepository.findById(idlibro).orElse(null);
        if (usuario == null || libro == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se encontró el libro seleccionado");
            return "redirect:/catalogo";
        }
        try {
            Prestamo prestamo = prestamoService.registrar(usuario, libro);
            redirectAttributes.addFlashAttribute("mensajeOk", "Préstamo registrado: '"
                    + libro.getTitulo() + "'. Devolución hasta el "
                    + PrestamoService.FORMATO_FECHA.format(prestamo.getFechadevolucion()) + ".");
        } catch (PrestamoInvalidoException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/catalogo";
    }
}
