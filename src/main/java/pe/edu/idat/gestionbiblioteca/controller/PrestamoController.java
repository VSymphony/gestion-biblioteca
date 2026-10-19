package pe.edu.idat.gestionbiblioteca.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.idat.gestionbiblioteca.dto.Respuesta;
import pe.edu.idat.gestionbiblioteca.model.Libro;
import pe.edu.idat.gestionbiblioteca.model.Prestamo;
import pe.edu.idat.gestionbiblioteca.model.Usuario;
import pe.edu.idat.gestionbiblioteca.repository.LibroRepository;
import pe.edu.idat.gestionbiblioteca.repository.PrestamoRepository;
import pe.edu.idat.gestionbiblioteca.repository.UsuarioRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/prestamo")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
public class PrestamoController {

    private static final int DIAS_PRESTAMO = 7;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/listar")
    public Respuesta listar() {
        return new Respuesta(true, null,
                prestamoRepository.findAll(Sort.by(Sort.Direction.DESC, "idprestamo")));
    }

    @GetMapping("/usuarios")
    public Respuesta usuarios() {
        return new Respuesta(true, null,
                usuarioRepository.findByActivoTrueOrderByNomusuario());
    }

    @GetMapping("/libros")
    public Respuesta libros() {
        return new Respuesta(true, null,
                libroRepository.findByStockGreaterThanOrderByTitulo(0));
    }

    @Transactional
    @PostMapping("/grabar")
    public Respuesta grabar(@RequestParam(required = false) Integer idusuario,
                            @RequestParam(required = false) Integer idlibro) {
        if (idusuario == null || idlibro == null) {
            return new Respuesta(false, "Datos incompletos");
        }
        Usuario usuario = usuarioRepository.findById(idusuario).orElse(null);
        if (usuario == null) {
            return new Respuesta(false, "El usuario no existe");
        }
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            return new Respuesta(false, "El usuario está desactivado");
        }
        Libro libro = libroRepository.findById(idlibro).orElse(null);
        if (libro == null) {
            return new Respuesta(false, "El libro no existe");
        }
        if (libro.getStock() == null || libro.getStock() <= 0) {
            return new Respuesta(false, "El libro no tiene stock disponible");
        }
        if (prestamoRepository.existsByUsuarioIdusuarioAndLibroIdlibroAndFechadevueltoIsNull(
                idusuario, idlibro)) {
            return new Respuesta(false, "El usuario ya tiene un préstamo pendiente de este libro");
        }
        LocalDate hoy = LocalDate.now();
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaprestamo(hoy);
        prestamo.setFechadevolucion(hoy.plusDays(DIAS_PRESTAMO));
        prestamoRepository.save(prestamo);
        libro.setStock(libro.getStock() - 1);
        libroRepository.save(libro);
        return new Respuesta(true, "Préstamo registrado. Devolución hasta el "
                + FORMATO_FECHA.format(prestamo.getFechadevolucion()));
    }

    @Transactional
    @PostMapping("/devolver")
    public Respuesta devolver(@RequestParam(required = false) Integer idprestamo) {
        if (idprestamo == null) {
            return new Respuesta(false, "Datos incompletos");
        }
        Prestamo prestamo = prestamoRepository.findById(idprestamo).orElse(null);
        if (prestamo == null) {
            return new Respuesta(false, "El préstamo no existe");
        }
        if (prestamo.getFechadevuelto() != null) {
            return new Respuesta(false, "El préstamo ya fue devuelto");
        }
        prestamo.setFechadevuelto(LocalDate.now());
        prestamoRepository.save(prestamo);
        Libro libro = prestamo.getLibro();
        if (libro != null) {
            libro.setStock((libro.getStock() == null ? 0 : libro.getStock()) + 1);
            libroRepository.save(libro);
        }
        return new Respuesta(true, "Devolución registrada correctamente");
    }
}
