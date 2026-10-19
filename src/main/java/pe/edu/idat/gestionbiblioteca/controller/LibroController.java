package pe.edu.idat.gestionbiblioteca.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.idat.gestionbiblioteca.dto.Respuesta;
import pe.edu.idat.gestionbiblioteca.model.Categoria;
import pe.edu.idat.gestionbiblioteca.model.Libro;
import pe.edu.idat.gestionbiblioteca.repository.CategoriaRepository;
import pe.edu.idat.gestionbiblioteca.repository.LibroRepository;
import pe.edu.idat.gestionbiblioteca.repository.PrestamoRepository;

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
    public Respuesta grabar(@RequestParam(required = false) Integer idlibro,
                            @RequestParam(required = false) String titulo,
                            @RequestParam(required = false) String autor,
                            @RequestParam(required = false) String isbn,
                            @RequestParam(required = false) Integer anio,
                            @RequestParam(required = false) Integer stock,
                            @RequestParam(required = false) Integer idcategoria) {
        titulo = titulo == null ? "" : titulo.trim();
        if (titulo.isBlank()) {
            return new Respuesta(false, "El título es obligatorio");
        }
        if (stock == null || stock < 0) {
            return new Respuesta(false, "El stock debe ser un número mayor o igual a cero");
        }
        Categoria categoria = idcategoria == null
                ? null
                : categoriaRepository.findById(idcategoria).orElse(null);
        if (categoria == null) {
            return new Respuesta(false, "La categoría seleccionada no existe");
        }
        Libro libro;
        if (idlibro == null) {
            libro = new Libro();
        } else {
            libro = libroRepository.findById(idlibro).orElse(null);
            if (libro == null) {
                return new Respuesta(false, "El libro no existe");
            }
        }
        libro.setTitulo(titulo);
        libro.setAutor(autor == null ? null : autor.trim());
        libro.setIsbn(isbn == null ? null : isbn.trim());
        libro.setAnio(anio);
        libro.setStock(stock);
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
}
