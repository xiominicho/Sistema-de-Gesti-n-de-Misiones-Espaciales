package modelo;

import java.io.Serializable;
import java.util.Objects;

public class AgenciaEspacial implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private String paisOrigen;
    private int anioFundacion;
    private String emailContacto;

    public AgenciaEspacial(int id, String nombre, String paisOrigen, int anioFundacion, String emailContacto) {
        this.id = id;
        this.nombre = nombre;
        this.paisOrigen = paisOrigen;
        this.anioFundacion = anioFundacion;
        this.emailContacto = emailContacto;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getPaisOrigen() { return paisOrigen; }
    public int getAnioFundacion() { return anioFundacion; }
    public String getEmailContacto() { return emailContacto; }

    @Override
    public String toString() {
        return nombre + " (" + paisOrigen + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgenciaEspacial that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}