package pe.edu.idat.gestionbiblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.idat.gestionbiblioteca.model.Usuario;

import java.util.List;

public interface UsuarioRepository
        extends JpaRepository<Usuario,Integer> {
    //select * from usuario where nomusuario=?
    Usuario findByNomusuario(String nomusuario);

    List<Usuario> findByActivoTrueAndRolNomrolOrderByNomusuario(String nomrol);
}
