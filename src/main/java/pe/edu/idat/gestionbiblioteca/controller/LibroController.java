package pe.edu.idat.gestionbiblioteca.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.idat.gestionbiblioteca.dto.LibroDto;
import pe.edu.idat.gestionbiblioteca.dto.Respuesta;
import pe.edu.idat.gestionbiblioteca.model.Categoria;
import pe.edu.idat.gestionbiblioteca.model.Libro;
import pe.edu.idat.gestionbiblioteca.repository.CategoriaRepository;
import pe.edu.idat.gestionbiblioteca.repository.LibroRepository;
import pe.edu.idat.gestionbiblioteca.repository.PrestamoRepository;
import pe.edu.idat.gestionbiblioteca.util.Validacion;

@RestController
@RequestMapping("/libro")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
public class LibroController {

    private final LibroRepository libroRepository;
    private final CategoriaRepository categoriaRepository;
    private final PrestamoRepository prestamoRepository;

    @GetMapping("/listar")
    public Respuesta listar() {
        return new Respuesta(true, null, libroRepository.findAll(Sort.by("titulo")));
    }

    @PostMapping("/grabar")
    public Respuesta grabar(@Valid @ModelAttribute LibroDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new Respuesta(false, Validacion.primerMensaje(bindingResult));
        }
        Categoria categoria = categoriaRepository.findById(dto.getIdcategoria()).orElse(null);
        if (categoria == null) {
            return new Respuesta(false, "La categoría seleccionada no existe");
        }
        Libro libro;
        if (dto.getIdlibro() == null) {
            libro = new Libro();
        } else {
            libro = libroRepository.findById(dto.getIdlibro()).orElse(null);
            if (libro == null) {
                return new Respuesta(false, "El libro no existe");
            }
        }
        libro.setTitulo(dto.getTitulo().trim());
        libro.setAutor(limpiar(dto.getAutor()));
        libro.setIsbn(limpiar(dto.getIsbn()));
        libro.setAnio(dto.getAnio());
        libro.setStock(dto.getStock());
        libro.setCategoria(categoria);
        libroRepository.save(libro);
        return new Respuesta(true, "Libro guardado correctamente");
    }

    @PostMapping("/eliminar")
    public Respuesta eliminar(@RequestParam(required = false) Integer idlibro) {
        if (idlibro == null || !libroRepository.existsById(idlibro)) {
            return new Respuesta(false, "El libro no existe");
        }
        if (prestamoRepository.existsByLibroIdlibro(idlibro)) {
            return new Respuesta(false, "No se puede eliminar: el libro tiene préstamos registrados");
        }
        libroRepository.deleteById(idlibro);
        return new Respuesta(true, "Libro eliminado correctamente");
    }

    private String limpiar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
