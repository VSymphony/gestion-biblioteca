package pe.edu.idat.gestionbiblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.idat.gestionbiblioteca.model.Rol;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByNomrol(String nomrol);
}
