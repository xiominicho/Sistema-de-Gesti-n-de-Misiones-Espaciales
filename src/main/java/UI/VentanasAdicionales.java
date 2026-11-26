package UI;

import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.MisionEspacial;

public class VentanasAdicionales {

    public static void mostrarVentanaAltoPresupuesto(Stage owner, String rutaTxt, List<MisionEspacial> filtradas, String contenidoTxt) {
        Stage ventana = new Stage();
        ventana.initOwner(owner);
        ventana.initModality(Modality.WINDOW_MODAL);
        ventana.setTitle("Misiones de Alto Presupuesto (> 100M USD)");

        Label titulo = new Label("Misiones con presupuesto superior a 100 millones de dólares");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblRuta = new Label("Archivo exportado: " + rutaTxt + " (" + filtradas.size() + " misión/es)");

        TextArea areaTexto = new TextArea();
        areaTexto.setEditable(false);
        areaTexto.setPrefRowCount(14);
        areaTexto.setPrefColumnCount(65);
        areaTexto.setText(contenidoTxt);

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> ventana.close());

        VBox layout = new VBox(10, titulo, lblRuta, areaTexto, btnCerrar);
        layout.setPadding(new Insets(15));

        ventana.setScene(new Scene(layout));
        ventana.showAndWait();
    }

    public static void mostrarVentanaPresupuestoTotal(Stage owner, double total, int cantidadMisiones) {
        Stage ventana = new Stage();
        ventana.initOwner(owner);
        ventana.initModality(Modality.WINDOW_MODAL);
        ventana.setTitle("Presupuesto Total de Misiones");

        Label titulo = new Label("Presupuesto Total Registrado");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        Label lblCantidad = new Label("Cantidad de misiones registradas: " + cantidadMisiones);
        Label lblTotal = new Label(String.format("Presupuesto total acumulado: USD %.2f", total));
        lblTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> ventana.close());

        VBox layout = new VBox(12, titulo, lblCantidad, lblTotal, btnCerrar);
        layout.setPadding(new Insets(18));

        ventana.setScene(new Scene(layout, 380, 170));
        ventana.showAndWait();
    }
}