package modelo;

import excepciones.DatoInvalidoException;
import java.util.Objects;

public class MisionNoTripulada extends MisionEspacial {
    private static final long serialVersionUID = 1L;

    private TipoDispositivo tipoDispositivo;

    public MisionNoTripulada(int idMision, String nombre, String destino, double presupuesto,
                             int duracionDias, AgenciaEspacial agencia, TipoDispositivo tipoDispositivo) {
        super(idMision, nombre, destino, presupuesto, duracionDias, agencia);
        setTipoDispositivo(tipoDispositivo);
    }

    public TipoDispositivo getTipoDispositivo() {
        return tipoDispositivo;
    }

    public void setTipoDispositivo(TipoDispositivo tipoDispositivo) {
        this.tipoDispositivo = Objects.requireNonNullElseGet(tipoDispositivo, () -> {
            throw new DatoInvalidoException("Debe indicar el tipo de dispositivo.");
        });
    }

    @Override
    public String getTipoMision() {
        return "No Tripulada";
    }

    @Override
    public String getDetalleEspecifico() {
        return "Dispositivo: " + tipoDispositivo;
    }
}