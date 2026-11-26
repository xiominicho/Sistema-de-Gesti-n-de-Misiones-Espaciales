package persistencia;

import excepciones.DatoInvalidoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import modelo.AgenciaEspacial;

public class CargadorAgencias {

    public List<AgenciaEspacial> cargarDesdeArchivo(String rutaArchivo) throws IOException {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            throw new IOException("No se encontró el archivo de agencias: " + rutaArchivo);
        }

        String nombreMinuscula = archivo.getName().toLowerCase();
        if (nombreMinuscula.endsWith(".csv")) {
            return cargarCsv(archivo);
        } else if (nombreMinuscula.endsWith(".json")) {
            return cargarJson(archivo);
        } else {
            throw new DatoInvalidoException("Formato no soportado. Use agencias.csv o agencias.json");
        }
    }

    private List<AgenciaEspacial> cargarCsv(File archivo) throws IOException {
        List<AgenciaEspacial> agencias = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                if (primeraLinea && linea.toLowerCase().contains("id") && linea.toLowerCase().contains("nombre")) {
                    primeraLinea = false;
                    continue;
                }
                primeraLinea = false;

                String[] partes = linea.split(",");
                if (partes.length < 5) {
                    throw new DatoInvalidoException("Línea CSV inválida: " + linea);
                }

                int id = Integer.parseInt(partes[0].trim());
                String nombre = partes[1].trim().replace("\"", "");
                String pais = partes[2].trim().replace("\"", "");
                int anio = Integer.parseInt(partes[3].trim());
                String email = partes[4].trim().replace("\"", "");

                agencias.add(new AgenciaEspacial(id, nombre, pais, anio, email));
            }
        }
        return agencias;
    }

    private List<AgenciaEspacial> cargarJson(File archivo) throws IOException {
        String contenido = Files.readString(archivo.toPath(), StandardCharsets.UTF_8);
        List<AgenciaEspacial> agencias = new ArrayList<>();

        Pattern objetoPattern = Pattern.compile("\\{[^{}]*\\}");
        Matcher objetoMatcher = objetoPattern.matcher(contenido);

        while (objetoMatcher.find()) {
            String obj = objetoMatcher.group();
            int id = extraerInt(obj, "id");
            String nombre = extraerString(obj, "nombre");
            String pais = extraerStringCualquiera(obj, new String[]{"paisOrigen", "pais_origen", "pais"});
            int anio = extraerIntCualquiera(obj, new String[]{"anioFundacion", "anio_fundacion", "anoFundacion", "anio"});
            String email = extraerStringCualquiera(obj, new String[]{"emailContacto", "email_contacto", "email"});

            agencias.add(new AgenciaEspacial(id, nombre, pais, anio, email));
        }

        if (agencias.isEmpty()) {
            throw new DatoInvalidoException("No se pudieron leer agencias desde el JSON.");
        }
        return agencias;
    }

    private int extraerInt(String jsonObj, String clave) {
        Matcher m = Pattern.compile("\"" + clave + "\"\\s*:\\s*(-?\\d+)").matcher(jsonObj);
        if (!m.find()) {
            throw new DatoInvalidoException("Falta el campo numérico '" + clave + "' en JSON.");
        }
        return Integer.parseInt(m.group(1));
    }

    private int extraerIntCualquiera(String jsonObj, String[] claves) {
        for (String clave : claves) {
            Matcher m = Pattern.compile("\"" + clave + "\"\\s*:\\s*(-?\\d+)").matcher(jsonObj);
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        }
        throw new DatoInvalidoException("Falta campo numérico en JSON: " + String.join("/", claves));
    }

    private String extraerString(String jsonObj, String clave) {
        Matcher m = Pattern.compile("\"" + clave + "\"\\s*:\\s*\"([^\"]*)\"").matcher(jsonObj);
        if (!m.find()) {
            throw new DatoInvalidoException("Falta el campo de texto '" + clave + "' en JSON.");
        }
        return m.group(1);
    }

    private String extraerStringCualquiera(String jsonObj, String[] claves) {
        for (String clave : claves) {
            Matcher m = Pattern.compile("\"" + clave + "\"\\s*:\\s*\"([^\"]*)\"").matcher(jsonObj);
            if (m.find()) {
                return m.group(1);
            }
        }
        throw new DatoInvalidoException("Falta campo de texto en JSON: " + String.join("/", claves));
    }
}