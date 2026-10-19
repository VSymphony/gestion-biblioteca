package pe.edu.idat.gestionbiblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.idat.gestionbiblioteca.model.Libro;

public interface LibroRepository extends JpaRepository<Libro, Integer> {

    boolean existsByCategoriaIdcategoria(Integer idcategoria);
}
