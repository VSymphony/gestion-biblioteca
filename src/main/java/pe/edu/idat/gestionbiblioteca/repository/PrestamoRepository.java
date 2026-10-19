package pe.edu.idat.gestionbiblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.idat.gestionbiblioteca.model.Prestamo;
import pe.edu.idat.gestionbiblioteca.model.Usuario;

import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {

    boolean existsByLibroIdlibro(Integer idlibro);

    boolean existsByUsuarioIdusuarioAndLibroIdlibroAndFechadevueltoIsNull(
            Integer idusuario, Integer idlibro);

    List<Prestamo> findByUsuarioOrderByIdprestamoDesc(Usuario usuario);
}
