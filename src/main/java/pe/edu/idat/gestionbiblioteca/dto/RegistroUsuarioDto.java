package pe.edu.idat.gestionbiblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Datos del formulario de registro validados en el servidor antes de crear el usuario.
@Getter
@Setter
public class RegistroUsuarioDto {

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 15, message = "Los nombres deben tener entre 2 y 15 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 15, message = "Los apellidos deben tener entre 2 y 15 caracteres")
    private String apellidos;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 4, max = 15, message = "El usuario debe tener entre 4 y 15 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
            message = "El usuario solo admite letras, números, punto, guion y guion bajo")
    private String nomusuario;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 50, message = "El email admite hasta 50 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 30, message = "La contraseña debe tener entre 8 y 30 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "La contraseña debe incluir al menos una letra y un número")
    private String password;
}
