package pe.edu.idat.gestionbiblioteca.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.idat.gestionbiblioteca.exception.PrestamoInvalidoException;
import pe.edu.idat.gestionbiblioteca.model.Libro;
import pe.edu.idat.gestionbiblioteca.model.Prestamo;
import pe.edu.idat.gestionbiblioteca.model.Usuario;
import pe.edu.idat.gestionbiblioteca.repository.LibroRepository;
import pe.edu.idat.gestionbiblioteca.repository.PrestamoRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    public static final int DIAS_PRESTAMO = 7;
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;

    // Crea el prestamo descontando stock. Lanza PrestamoInvalidoException si no cumple las reglas.
    @Transactional
    public Prestamo registrar(Usuario usuario, Libro libro) {
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new PrestamoInvalidoException("El usuario está desactivado");
        }
        if (libro.getStock() == null || libro.getStock() <= 0) {
            throw new PrestamoInvalidoException("El libro no tiene stock disponible");
        }
        if (prestamoRepository.existsByUsuarioIdusuarioAndLibroIdlibroAndFechadevueltoIsNull(
                usuario.getIdusuario(), libro.getIdlibro())) {
            throw new PrestamoInvalidoException("El usuario ya tiene un préstamo pendiente de este libro");
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
        return prestamo;
    }
}
