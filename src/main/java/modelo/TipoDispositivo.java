package modelo;

public enum TipoDispositivo {
    SATELITE("Satélite"),
    SONDA_ESPACIAL("Sonda espacial"),
    ROVER("Rover");

    private final String etiqueta;

    TipoDispositivo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
