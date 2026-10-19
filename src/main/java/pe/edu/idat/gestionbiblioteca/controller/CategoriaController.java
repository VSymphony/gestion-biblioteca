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
import pe.edu.idat.gestionbiblioteca.repository.CategoriaRepository;
import pe.edu.idat.gestionbiblioteca.repository.LibroRepository;

@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;
    private final LibroRepository libroRepository;

    @GetMapping("/listar")
    public Respuesta listar() {
        return new Respuesta(true, null, categoriaRepository.findAll(Sort.by("nombre")));
    }

    @PostMapping("/grabar")
    public Respuesta grabar(@RequestParam(required = false) Integer idcategoria,
                            @RequestParam(required = false) String nombre,
                            @RequestParam(required = false) String descripcion) {
        nombre = nombre == null ? "" : nombre.trim();
        if (nombre.isBlank()) {
            return new Respuesta(false, "El nombre es obligatorio");
        }
        Categoria categoria;
        if (idcategoria == null) {
            if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
                return new Respuesta(false, "Ya existe una categoría con ese nombre");
            }
            categoria = new Categoria();
        } else {
            categoria = categoriaRepository.findById(idcategoria).orElse(null);
            if (categoria == null) {
                return new Respuesta(false, "La categoría no existe");
            }
            if (!nombre.equalsIgnoreCase(categoria.getNombre())
                    && categoriaRepository.existsByNombreIgnoreCase(nombre)) {
                return new Respuesta(false, "Ya existe una categoría con ese nombre");
            }
        }
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion == null ? null : descripcion.trim());
        categoriaRepository.save(categoria);
        return new Respuesta(true, "Categoría guardada correctamente");
    }

    @PostMapping("/eliminar")
    public Respuesta eliminar(@RequestParam(required = false) Integer idcategoria) {
        if (idcategoria == null || !categoriaRepository.existsById(idcategoria)) {
            return new Respuesta(false, "La categoría no existe");
        }
        if (libroRepository.existsByCategoriaIdcategoria(idcategoria)) {
            return new Respuesta(false, "No se puede eliminar: la categoría tiene libros asociados");
        }
        categoriaRepository.deleteById(idcategoria);
        return new Respuesta(true, "Categoría eliminada correctamente");
    }
}
