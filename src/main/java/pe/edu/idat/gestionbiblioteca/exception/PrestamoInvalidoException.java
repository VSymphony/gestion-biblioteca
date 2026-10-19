package pe.edu.idat.gestionbiblioteca.exception;

// Regla de negocio incumplida al registrar un prestamo.
public class PrestamoInvalidoException extends RuntimeException {

    public PrestamoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
