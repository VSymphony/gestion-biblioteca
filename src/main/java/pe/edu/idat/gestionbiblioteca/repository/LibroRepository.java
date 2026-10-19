package pe.edu.idat.gestionbiblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.idat.gestionbiblioteca.model.Libro;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Integer> {

    boolean existsByCategoriaIdcategoria(Integer idcategoria);

    List<Libro> findByStockGreaterThanOrderByTitulo(Integer stock);

    List<Libro> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrderByTitulo(
            String titulo, String autor);
}
