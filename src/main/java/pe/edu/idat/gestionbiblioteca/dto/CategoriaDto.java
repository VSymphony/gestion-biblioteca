package pe.edu.idat.gestionbiblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Datos del mantenimiento de categorías validados en el servidor.
@Getter
@Setter
public class CategoriaDto {

    private Integer idcategoria;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción admite hasta 255 caracteres")
    private String descripcion;
}
