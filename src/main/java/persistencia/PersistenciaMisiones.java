package persistencia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import modelo.MisionEspacial;

public class PersistenciaMisiones {

    public static final double UMBRAL_ALTO_PRESUPUESTO = 100_000_000.0;

    public void guardarBinario(String rutaArchivo, List<MisionEspacial> misiones) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(new ArrayList<>(misiones));
        }
    }

    @SuppressWarnings("unchecked")
    public List<MisionEspacial> cargarBinario(String rutaArchivo) throws IOException, ClassNotFoundException {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                return (List<MisionEspacial>) obj;
            }
            return new ArrayList<>();
        }
    }

    public void exportarAltoPresupuestoTxt(String rutaTxt, List<MisionEspacial> misionesAltoPresupuesto) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaTxt, StandardCharsets.UTF_8))) {
            bw.write("=== MISIONES ESPACIALES DE ALTO PRESUPUESTO (> USD 100.000.000) ===");
            bw.newLine();
            bw.write("------------------------------------------------------------------");
            bw.newLine();
            if (misionesAltoPresupuesto.isEmpty()) {
                bw.write("No hay misiones registradas con presupuesto superior a 100 millones de dólares.");
                bw.newLine();
            } else {
                for (MisionEspacial m : misionesAltoPresupuesto) {
                    bw.write(m.formatearResumen());
                    bw.newLine();
                }
            }
        }
    }
}