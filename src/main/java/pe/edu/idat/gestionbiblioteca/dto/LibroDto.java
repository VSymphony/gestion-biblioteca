package pe.edu.idat.gestionbiblioteca.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Datos del mantenimiento de libros validados en el servidor.
@Getter
@Setter
public class LibroDto {

    private Integer idlibro;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título admite hasta 200 caracteres")
    private String titulo;

    @Size(max = 150, message = "El autor admite hasta 150 caracteres")
    private String autor;

    @Size(max = 20, message = "El ISBN admite hasta 20 caracteres")
    private String isbn;

    @Min(value = 0, message = "El año debe ser mayor o igual a cero")
    @Max(value = 2100, message = "El año no puede superar 2100")
    private Integer anio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock debe ser un número mayor o igual a cero")
    @Max(value = 9999, message = "El stock no puede superar 9999")
    private Integer stock;

    @NotNull(message = "La categoría es obligatoria")
    private Integer idcategoria;
}
