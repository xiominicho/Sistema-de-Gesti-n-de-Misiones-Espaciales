package modelo;

import excepciones.DatoInvalidoException;

public class MisionTripulada extends MisionEspacial {
    private static final long serialVersionUID = 1L;

    private int cantidadAstronautas;

    public MisionTripulada(int idMision, String nombre, String destino, double presupuesto,
                           int duracionDias, AgenciaEspacial agencia, int cantidadAstronautas) {
        super(idMision, nombre, destino, presupuesto, duracionDias, agencia);
        setCantidadAstronautas(cantidadAstronautas);
    }

    public int getCantidadAstronautas() {
        return cantidadAstronautas;
    }

    public void setCantidadAstronautas(int cantidadAstronautas) {
        if (cantidadAstronautas <= 0) {
            throw new DatoInvalidoException("Una misión tripulada debe tener al menos 1 astronauta.");
        }
        this.cantidadAstronautas = cantidadAstronautas;
    }

    @Override
    public String getTipoMision() {
        return "Tripulada";
    }

    @Override
    public String getDetalleEspecifico() {
        return "Astronautas: " + cantidadAstronautas;
    }
}