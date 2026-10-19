package pe.edu.idat.gestionbiblioteca.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.idat.gestionbiblioteca.model.Libro;
import pe.edu.idat.gestionbiblioteca.repository.LibroRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CatalogoController {

    private final LibroRepository libroRepository;

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
}
