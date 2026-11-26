package excepciones;

public class MisionDuplicadaException extends Exception {
    public MisionDuplicadaException(int idMision) {
        super("Ya existe una misión registrada con el ID: " + idMision);
    }
}
