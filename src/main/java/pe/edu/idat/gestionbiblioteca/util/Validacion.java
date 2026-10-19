package pe.edu.idat.gestionbiblioteca.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

// Extrae el primer mensaje de error de validación para responder al AJAX.
public final class Validacion {

    private Validacion() {
    }

    public static String primerMensaje(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Datos inválidos");
    }
}
