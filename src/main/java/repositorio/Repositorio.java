package repositorio;

import excepciones.MisionDuplicadaException;
import excepciones.MisionNoEncontradaException;
import java.util.List;

public interface Repositorio<T, ID> {
    void agregar(T entidad) throws MisionDuplicadaException;
    void modificar(T entidad) throws MisionNoEncontradaException;
    void eliminar(ID id) throws MisionNoEncontradaException;
    T buscarPorId(ID id) throws MisionNoEncontradaException;
    List<T> listarTodos();
    void reemplazarTodos(List<T> nuevasEntidades);
}
