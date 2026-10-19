package pe.edu.idat.gestionbiblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.idat.gestionbiblioteca.model.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {

    boolean existsByLibroIdlibro(Integer idlibro);
}
