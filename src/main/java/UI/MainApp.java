package UI;

import excepciones.DatoInvalidoException;
import excepciones.MisionDuplicadaException;
import excepciones.MisionNoEncontradaException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.AgenciaEspacial;
import modelo.MisionEspacial;
import modelo.MisionNoTripulada;
import modelo.MisionTripulada;
import modelo.TipoDispositivo;
import persistencia.CargadorAgencias;
import persistencia.PersistenciaMisiones;
import repositorio.GestorMisiones;

public class MainApp extends Application {

    private static final String ARCHIVO_MISIONES_BIN = "misiones.bin";
    private static final String ARCHIVO_ALTO_PRESUPUESTO_TXT = "misiones_alto_presupuesto.txt";

    private final GestorMisiones gestor = new GestorMisiones();
    private final CargadorAgencias cargadorAgencias = new CargadorAgencias();
    private final PersistenciaMisiones persistencia = new PersistenciaMisiones();

    private final ObservableList<MisionEspacial> datosTabla = FXCollections.observableArrayList();
    private TableView<MisionEspacial> tablaMisiones;

    private TextField txtId;
    private TextField txtNombre;
    private TextField txtDestino;
    private TextField txtPresupuesto;
    private TextField txtDuracion;
    private ComboBox<AgenciaEspacial> cmbAgencia;

    private RadioButton rbTripulada;
    private RadioButton rbNoTripulada;
    private Label lblCampoEspecifico;
    private TextField txtAstronautas;
    private ComboBox<TipoDispositivo> cmbDispositivo;

    private Stage stagePrincipal;

    @Override
    public void start(Stage primaryStage) {
        this.stagePrincipal = primaryStage;
        primaryStage.setTitle("Sistema de Gestión de Misiones Espaciales");

        cargarAgenciasIniciales();
        cargarMisionesGuardadas();

        VBox formulario = crearFormulario();
        tablaMisiones = crearTabla();
        HBox botonesCrud = crearBotonesCrud();
        HBox botonesExtra = crearBotonesAdicionales();

        refrescarTabla();

        VBox root = new VBox(12, formulario, botonesCrud, tablaMisiones, botonesExtra);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 980, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void cargarAgenciasIniciales() {
        List<AgenciaEspacial> agencias = List.of();
        try {
            if (new File("agencias.json").exists()) {
                agencias = cargadorAgencias.cargarDesdeArchivo("agencias.json");
            } else if (new File("agencias.csv").exists()) {
                agencias = cargadorAgencias.cargarDesdeArchivo("agencias.csv");
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Archivo no encontrado",
                        "No se encontró agencias.json ni agencias.csv en la raíz del proyecto. Colocá uno de los archivos y reiniciá.");
            }
        } catch (IOException | DatoInvalidoException | NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar agencias", ex.getMessage());
        }

        cmbAgencia = new ComboBox<>();
        cmbAgencia.setItems(FXCollections.observableArrayList(agencias));
        if (!agencias.isEmpty()) {
            cmbAgencia.getSelectionModel().selectFirst();
        }
    }

    private void cargarMisionesGuardadas() {
        try {
            List<MisionEspacial> recuperadas = persistencia.cargarBinario(ARCHIVO_MISIONES_BIN);
            gestor.reemplazarTodos(recuperadas);
        } catch (IOException | ClassNotFoundException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso de persistencia",
                    "No se pudieron recuperar misiones previas: " + ex.getMessage());
        }
    }

    private VBox crearFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);

        txtId = new TextField();
        txtId.setPromptText("Ej: 101");

        txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la misión");

        txtDestino = new TextField();
        txtDestino.setPromptText("Ej: Marte, Luna, Órbita");

        txtPresupuesto = new TextField();
        txtPresupuesto.setPromptText("En USD (Ej: 150000000)");

        txtDuracion = new TextField();
        txtDuracion.setPromptText("Días (Ej: 365)");

        ToggleGroup grupoTipo = new ToggleGroup();
        rbTripulada = new RadioButton("Tripulada");
        rbNoTripulada = new RadioButton("No Tripulada");
        rbTripulada.setToggleGroup(grupoTipo);
        rbNoTripulada.setToggleGroup(grupoTipo);
        rbTripulada.setSelected(true);

        lblCampoEspecifico = new Label("Cantidad de astronautas:");
        txtAstronautas = new TextField();
        txtAstronautas.setPromptText("Ej: 4");

        cmbDispositivo = new ComboBox<>(FXCollections.observableArrayList(TipoDispositivo.values()));
        cmbDispositivo.getSelectionModel().selectFirst();
        cmbDispositivo.setVisible(false);
        cmbDispositivo.setManaged(false);

        rbTripulada.setOnAction(e -> alternarCamposTipo());
        rbNoTripulada.setOnAction(e -> alternarCamposTipo());

        grid.add(new Label("ID Misión:"), 0, 0);
        grid.add(txtId, 1, 0);
        grid.add(new Label("Nombre:"), 2, 0);
        grid.add(txtNombre, 3, 0);

        grid.add(new Label("Destino:"), 0, 1);
        grid.add(txtDestino, 1, 1);
        grid.add(new Label("Presupuesto (USD):"), 2, 1);
        grid.add(txtPresupuesto, 3, 1);

        grid.add(new Label("Duración (días):"), 0, 2);
        grid.add(txtDuracion, 1, 2);
        grid.add(new Label("Agencia espacial:"), 2, 2);
        grid.add(cmbAgencia, 3, 2);

        HBox boxTipo = new HBox(12, rbTripulada, rbNoTripulada);
        HBox boxEspecifico = new HBox(8, txtAstronautas, cmbDispositivo);

        grid.add(new Label("Tipo de misión:"), 0, 3);
        grid.add(boxTipo, 1, 3);
        grid.add(lblCampoEspecifico, 2, 3);
        grid.add(boxEspecifico, 3, 3);

        VBox contenedor = new VBox(6, new Label("Datos de la Misión Espacial"), grid);
        contenedor.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        return contenedor;
    }

    private void alternarCamposTipo() {
        boolean esTripulada = rbTripulada.isSelected();
        lblCampoEspecifico.setText(esTripulada ? "Cantidad de astronautas:" : "Tipo de dispositivo:");

        txtAstronautas.setVisible(esTripulada);
        txtAstronautas.setManaged(esTripulada);

        cmbDispositivo.setVisible(!esTripulada);
        cmbDispositivo.setManaged(!esTripulada);
    }

    private TableView<MisionEspacial> crearTabla() {
        TableView<MisionEspacial> tabla = new TableView<>(datosTabla);
        tabla.setPrefHeight(280);

        TableColumn<MisionEspacial, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getIdMision())));

        TableColumn<MisionEspacial, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));

        TableColumn<MisionEspacial, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoMision()));

        TableColumn<MisionEspacial, String> colDestino = new TableColumn<>("Destino");
        colDestino.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDestino()));

        TableColumn<MisionEspacial, String> colAgencia = new TableColumn<>("Agencia");
        colAgencia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAgencia().getNombre()));

        TableColumn<MisionEspacial, String> colPresupuesto = new TableColumn<>("Presupuesto (USD)");
        colPresupuesto.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getPresupuesto())));

        TableColumn<MisionEspacial, String> colDuracion = new TableColumn<>("Duración (días)");
        colDuracion.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getDuracionDias())));

        TableColumn<MisionEspacial, String> colDetalle = new TableColumn<>("Detalle");
        colDetalle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDetalleEspecifico()));

        tabla.getColumns().addAll(colId, colNombre, colTipo, colDestino, colAgencia, colPresupuesto, colDuracion, colDetalle);

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, nuevaSel) -> {
            if (nuevaSel != null) {
                cargarEnFormulario(nuevaSel);
            }
        });

        return tabla;
    }

    private HBox crearBotonesCrud() {
        Button btnAgregar = new Button("Agregar");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Limpiar");
        Button btnGuardarArchivo = new Button("Guardar en archivo");

        btnAgregar.setOnAction(e -> agregarMision());
        btnModificar.setOnAction(e -> modificarMision());
        btnEliminar.setOnAction(e -> eliminarMision());
        btnLimpiar.setOnAction(e -> limpiarFormulario());
        btnGuardarArchivo.setOnAction(e -> guardarMisionesEnArchivo());

        return new HBox(10, btnAgregar, btnModificar, btnEliminar, btnLimpiar, btnGuardarArchivo);
    }

    private HBox crearBotonesAdicionales() {
        Button btnAltoPresupuesto = new Button("Ver misiones de alto presupuesto");
        Button btnPresupuestoTotal = new Button("Ver presupuesto total");

        btnAltoPresupuesto.setOnAction(e -> abrirVentanaAltoPresupuesto());
        btnPresupuestoTotal.setOnAction(e -> abrirVentanaPresupuestoTotal());

        return new HBox(12, btnAltoPresupuesto, btnPresupuestoTotal);
    }

    private MisionEspacial construirMisionDesdeFormulario(int idForzado) {
        String nombre = txtNombre.getText();
        String destino = txtDestino.getText();
        AgenciaEspacial agencia = cmbAgencia.getValue();

        double presupuesto;
        int duracion;
        try {
            presupuesto = Double.parseDouble(txtPresupuesto.getText().trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            throw new DatoInvalidoException("El presupuesto debe ser un número válido.");
        }

        try {
            duracion = Integer.parseInt(txtDuracion.getText().trim());
        } catch (NumberFormatException ex) {
            throw new DatoInvalidoException("La duración en días debe ser un número entero válido.");
        }

        if (rbTripulada.isSelected()) {
            int astronautas;
            try {
                astronautas = Integer.parseInt(txtAstronautas.getText().trim());
            } catch (NumberFormatException ex) {
                throw new DatoInvalidoException("La cantidad de astronautas debe ser un número entero válido.");
            }
            return new MisionTripulada(idForzado, nombre, destino, presupuesto, duracion, agencia, astronautas);
        } else {
            TipoDispositivo dispositivo = cmbDispositivo.getValue();
            return new MisionNoTripulada(idForzado, nombre, destino, presupuesto, duracion, agencia, dispositivo);
        }
    }

    private int leerIdDesdeCampo() {
        try {
            return Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException ex) {
            throw new DatoInvalidoException("El ID de misión debe ser un número entero válido.");
        }
    }

    private void agregarMision() {
        try {
            int id = leerIdDesdeCampo();
            MisionEspacial nueva = construirMisionDesdeFormulario(id);
            gestor.agregar(nueva);
            persistencia.guardarBinario(ARCHIVO_MISIONES_BIN, gestor.listarTodos());
            refrescarTabla();
            limpiarFormulario();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Misión agregada correctamente.");
        } catch (MisionDuplicadaException | DatoInvalidoException | IOException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al agregar", ex.getMessage());
        }
    }

    private void modificarMision() {
        MisionEspacial seleccionada = tablaMisiones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccioná una misión de la tabla para modificar.");
            return;
        }

        try {
            // Se conserva siempre el idMision original de la misión seleccionada
            int idOriginal = seleccionada.getIdMision();
            MisionEspacial actualizada = construirMisionDesdeFormulario(idOriginal);
            gestor.modificar(actualizada);
            persistencia.guardarBinario(ARCHIVO_MISIONES_BIN, gestor.listarTodos());
            refrescarTabla();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Misión #" + idOriginal + " modificada correctamente (el ID no se modifica).");
        } catch (MisionNoEncontradaException | DatoInvalidoException | IOException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al modificar", ex.getMessage());
        }
    }

    private void eliminarMision() {
        MisionEspacial seleccionada = tablaMisiones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccioná una misión de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Deseás eliminar la siguiente misión?");
        confirmacion.setContentText(seleccionada.formatearResumen());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                gestor.eliminar(seleccionada.getIdMision());
                persistencia.guardarBinario(ARCHIVO_MISIONES_BIN, gestor.listarTodos());
                refrescarTabla();
                limpiarFormulario();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminada", "La misión fue eliminada.");
            } catch (MisionNoEncontradaException | IOException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al eliminar", ex.getMessage());
            }
        }
    }

    private void guardarMisionesEnArchivo() {
        try {
            persistencia.guardarBinario(ARCHIVO_MISIONES_BIN, gestor.listarTodos());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado",
                    "Todas las misiones se guardaron en " + ARCHIVO_MISIONES_BIN);
        } catch (IOException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar", ex.getMessage());
        }
    }

    private void abrirVentanaAltoPresupuesto() {
        try {
            List<MisionEspacial> filtradas = gestor.obtenerMisionesAltoPresupuesto(PersistenciaMisiones.UMBRAL_ALTO_PRESUPUESTO);
            persistencia.exportarAltoPresupuestoTxt(ARCHIVO_ALTO_PRESUPUESTO_TXT, filtradas);
            String contenido = Files.readString(new File(ARCHIVO_ALTO_PRESUPUESTO_TXT).toPath(), StandardCharsets.UTF_8);
            VentanasAdicionales.mostrarVentanaAltoPresupuesto(stagePrincipal, ARCHIVO_ALTO_PRESUPUESTO_TXT, filtradas, contenido);
        } catch (IOException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al exportar/leer TXT", ex.getMessage());
        }
    }

    private void abrirVentanaPresupuestoTotal() {
        double total = gestor.calcularPresupuestoTotal();
        int cantidad = gestor.listarTodos().size();
        VentanasAdicionales.mostrarVentanaPresupuestoTotal(stagePrincipal, total, cantidad);
    }

    private void cargarEnFormulario(MisionEspacial m) {
        txtId.setText(String.valueOf(m.getIdMision()));
        txtNombre.setText(m.getNombre());
        txtDestino.setText(m.getDestino());
        txtPresupuesto.setText(String.valueOf(m.getPresupuesto()));
        txtDuracion.setText(String.valueOf(m.getDuracionDias()));
        cmbAgencia.setValue(m.getAgencia());

        if (m instanceof MisionTripulada tripulada) {
            rbTripulada.setSelected(true);
            alternarCamposTipo();
            txtAstronautas.setText(String.valueOf(tripulada.getCantidadAstronautas()));
        } else if (m instanceof MisionNoTripulada noTripulada) {
            rbNoTripulada.setSelected(true);
            alternarCamposTipo();
            cmbDispositivo.setValue(noTripulada.getTipoDispositivo());
        }
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        txtDestino.clear();
        txtPresupuesto.clear();
        txtDuracion.clear();
        txtAstronautas.clear();
        if (!cmbAgencia.getItems().isEmpty()) {
            cmbAgencia.getSelectionModel().selectFirst();
        }
        rbTripulada.setSelected(true);
        alternarCamposTipo();
        tablaMisiones.getSelectionModel().clearSelection();
    }

    private void refrescarTabla() {
        datosTabla.setAll(gestor.listarTodos());
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}