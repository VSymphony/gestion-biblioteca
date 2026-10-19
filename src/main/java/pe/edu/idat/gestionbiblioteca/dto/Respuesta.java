package pe.edu.idat.gestionbiblioteca.dto;

public record Respuesta(boolean respuesta, String mensaje, Object data) {

    public Respuesta(boolean respuesta, String mensaje) {
        this(respuesta, mensaje, null);
    }
}
