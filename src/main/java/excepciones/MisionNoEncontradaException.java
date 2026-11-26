package excepciones;

public class MisionNoEncontradaException extends Exception {
    public MisionNoEncontradaException(int idMision) {
        super("No se encontró ninguna misión con el ID: " + idMision);
    }
}