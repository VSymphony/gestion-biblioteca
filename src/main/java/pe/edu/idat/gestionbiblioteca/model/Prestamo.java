package pe.edu.idat.gestionbiblioteca.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "prestamo")
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idprestamo;

    @ManyToOne
    @JoinColumn(name = "idlibro")
    private Libro libro;

    @ManyToOne
    @JoinColumn(name = "idusuario")
    private Usuario usuario;

    private LocalDate fechaprestamo;

    private LocalDate fechadevolucion;

    private LocalDate fechadevuelto;
}
