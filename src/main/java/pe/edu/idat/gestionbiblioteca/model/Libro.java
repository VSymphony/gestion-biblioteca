package pe.edu.idat.gestionbiblioteca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "libro")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idlibro;

    @Column(length = 200)
    private String titulo;

    @Column(length = 150)
    private String autor;

    @Column(length = 20)
    private String isbn;

    private Integer anio;

    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "idcategoria")
    private Categoria categoria;
}
