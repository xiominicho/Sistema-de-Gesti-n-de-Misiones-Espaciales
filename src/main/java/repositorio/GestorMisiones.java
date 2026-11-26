package repositorio;

import excepciones.DatoInvalidoException;
import excepciones.MisionDuplicadaException;
import excepciones.MisionNoEncontradaException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modelo.MisionEspacial;

public class GestorMisiones implements Repositorio<MisionEspacial, Integer> {

    private final List<MisionEspacial> misiones = new ArrayList<>();

    @Override
    public void agregar(MisionEspacial mision) throws MisionDuplicadaException {
        if (mision == null) {
            throw new DatoInvalidoException("La misión no puede ser nula.");
        }
        if (existeId(mision.getIdMision())) {
            throw new MisionDuplicadaException(mision.getIdMision());
        }
        misiones.add(mision);
    }

    @Override
    public void modificar(MisionEspacial misionActualizada) throws MisionNoEncontradaException {
        if (misionActualizada == null) {
            throw new DatoInvalidoException("La misión no puede ser nula.");
        }
        for (int i = 0; i < misiones.size(); i++) {
            if (misiones.get(i).getIdMision() == misionActualizada.getIdMision()) {
                misiones.set(i, misionActualizada);
                return;
            }
        }
        throw new MisionNoEncontradaException(misionActualizada.getIdMision());
    }

    @Override
    public void eliminar(Integer id) throws MisionNoEncontradaException {
        MisionEspacial encontrada = buscarPorId(id);
        misiones.remove(encontrada);
    }

    @Override
    public MisionEspacial buscarPorId(Integer id) throws MisionNoEncontradaException {
        if (id == null) {
            throw new DatoInvalidoException("El ID de búsqueda no puede ser nulo.");
        }
        for (MisionEspacial m : misiones) {
            if (m.getIdMision() == id) {
                return m;
            }
        }
        throw new MisionNoEncontradaException(id);
    }

    @Override
    public List<MisionEspacial> listarTodos() {
        return Collections.unmodifiableList(misiones);
    }

    @Override
    public void reemplazarTodos(List<MisionEspacial> nuevasEntidades) {
        misiones.clear();
        if (nuevasEntidades != null) {
            misiones.addAll(nuevasEntidades);
        }
    }

    public boolean existeId(int idMision) {
        return misiones.stream().anyMatch(m -> m.getIdMision() == idMision);
    }

    public double calcularPresupuestoTotal() {
        double total = 0;
        for (MisionEspacial m : misiones) {
            total += m.getPresupuesto();
        }
        return total;
    }

    public List<MisionEspacial> obtenerMisionesAltoPresupuesto(double umbral) {
        List<MisionEspacial> filtradas = new ArrayList<>();
        for (MisionEspacial m : misiones) {
            if (m.getPresupuesto() > umbral) {
                filtradas.add(m);
            }
        }
        return filtradas;
    }
}
