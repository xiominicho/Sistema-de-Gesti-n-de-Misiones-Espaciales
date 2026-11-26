package modelo;

import excepciones.DatoInvalidoException;
import java.io.Serializable;

public abstract class MisionEspacial implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idMision;
    private String nombre;
    private String destino;
    private double presupuesto;
    private int duracionDias;
    private AgenciaEspacial agencia;

    public MisionEspacial(int idMision, String nombre, String destino,
                          double presupuesto, int duracionDias, AgenciaEspacial agencia) {
        validarDatosComunes(idMision, nombre, destino, presupuesto, duracionDias, agencia);
        this.idMision = idMision;
        this.nombre = nombre.trim();
        this.destino = destino.trim();
        this.presupuesto = presupuesto;
        this.duracionDias = duracionDias;
        this.agencia = agencia;
    }

    private void validarDatosComunes(int idMision, String nombre, String destino,
                                     double presupuesto, int duracionDias, AgenciaEspacial agencia) {
        if (idMision <= 0) {
            throw new DatoInvalidoException("El ID de misión debe ser mayor que 0.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre de la misión no puede estar vacío.");
        }
        if (destino == null || destino.isBlank()) {
            throw new DatoInvalidoException("El destino no puede estar vacío.");
        }
        if (presupuesto < 0) {
            throw new DatoInvalidoException("El presupuesto no puede ser negativo.");
        }
        if (duracionDias <= 0) {
            throw new DatoInvalidoException("La duración estimada debe ser mayor a 0 días.");
        }
        if (agencia == null) {
            throw new DatoInvalidoException("Debe seleccionar una agencia espacial.");
        }
    }

    public abstract String getTipoMision();
    public abstract String getDetalleEspecifico();

    public int getIdMision() { return idMision; }
    public String getNombre() { return nombre; }
    public String getDestino() { return destino; }
    public double getPresupuesto() { return presupuesto; }
    public int getDuracionDias() { return duracionDias; }
    public AgenciaEspacial getAgencia() { return agencia; }

    public String formatearResumen() {
        return String.format("[#%d] %s | %s | Destino: %s | Agencia: %s | Presupuesto: $%.2f | Duración: %d días | %s",
                idMision, nombre, getTipoMision(), destino, agencia.getNombre(),
                presupuesto, duracionDias, getDetalleEspecifico());
    }
}