package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.TableroDB;
import modelo.sqlite.JSqlite;

import java.util.ArrayList;

public class DialogoPartidas {

    private final Stage stage;
    private TableroDB partidaSeleccionada;
    private TableView<TableroDB> tabla;
    private ObservableList<TableroDB> datos;

    public DialogoPartidas(Stage owner) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Partidas guardadas");
        stage.setResizable(false);
        stage.setScene(new Scene(construirLayout(), 620, 380));
    }

    @SuppressWarnings("unchecked")
    private BorderPane construirLayout() {
        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TableroDB, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMaxWidth(50);

        TableColumn<TableroDB, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<TableroDB, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        TableColumn<TableroDB, String> colJugadores = new TableColumn<>("Jugadores");
        colJugadores.setCellValueFactory(new PropertyValueFactory<>("jugadores"));

        TableColumn<TableroDB, Boolean> colTerminada = new TableColumn<>("¿Terminada?");
        colTerminada.setCellValueFactory(new PropertyValueFactory<>("partidaTerminada"));
        colTerminada.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : (item ? "Sí" : "No"));
            }
        });

        tabla.getColumns().addAll(colId, colNombre, colFecha, colJugadores, colTerminada);
        cargarDatos();

        Button btnCargar = new Button("Cargar partida");
        Button btnEliminar = new Button("Eliminar partida");
        Button btnCancelar = new Button("Cancelar");

        btnCargar.setStyle("-fx-background-color: #1a6ef5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
        btnEliminar.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
        btnCancelar.setStyle("-fx-background-color: #aaaaaa; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");

        btnCargar.setOnAction(e -> accionCargar());
        btnEliminar.setOnAction(e -> accionEliminar());
        btnCancelar.setOnAction(e -> {
            partidaSeleccionada = null;
            stage.close();
        });

        HBox panelBotones = new HBox(12, btnCargar, btnEliminar, btnCancelar);
        panelBotones.setAlignment(Pos.CENTER);
        panelBotones.setPadding(new Insets(12));

        BorderPane root = new BorderPane();
        root.setCenter(tabla);
        root.setBottom(panelBotones);
        root.setPadding(new Insets(10));
        return root;
    }

    private void cargarDatos() {
        ArrayList<TableroDB> lista = JSqlite.getInstance().getPartidas();
        datos = FXCollections.observableArrayList(lista);
        tabla.setItems(datos);
    }

    private void accionCargar() {
        TableroDB seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAviso("Selecciona una partida primero.");
            return;
        }
        partidaSeleccionada = seleccionada;
        IO.println("Partida seleccionada para cargar: " + seleccionada.getNombre());
        stage.close();
    }

    private void accionEliminar() {
        TableroDB seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAviso("Selecciona una partida para eliminar.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar la partida \"" + seleccionada.getNombre() + "\"?");
        confirm.initModality(Modality.APPLICATION_MODAL);
        confirm.showAndWait().ifPresent(tipo -> {
            if (tipo == ButtonType.OK) {
                JSqlite.getInstance().delete(seleccionada.getId());
                cargarDatos();
                IO.println("Partida eliminada: ID " + seleccionada.getId());
            }
        });
    }

    private void mostrarAviso(String mensaje) {
        Alert aviso = new Alert(Alert.AlertType.WARNING);
        aviso.setHeaderText(mensaje);
        aviso.initModality(Modality.APPLICATION_MODAL);
        aviso.showAndWait();
    }

    public void showAndWait() {
        stage.showAndWait();
    }

    public TableroDB getPartidaSeleccionada() {
        return partidaSeleccionada;
    }
}
