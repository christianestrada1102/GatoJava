package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import modelo.TableroDB;
import modelo.sqlite.JSqlite;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class tabController {

    private Tablero tablero;
    private String simbolo;
    private String nombreJugador;
    private int victoriasJugador;
    private int victoriasComputadora;
    private int empates;
    private Button[][] botonesTablero;

    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private GridPane gridTablero;
    @FXML private Label lblCierre;
    @FXML private Label lblNombreJugador;
    @FXML private Label lblMarcadorJugador;
    @FXML private Label lblMarcadorCompu;
    @FXML private Label lblEmpates;
    @FXML private Label lblTurno;
    @FXML private ImageView imgJugador;
    @FXML private ImageView imgComputadora;

    private JSqlite db;
    private int idPartidaActual = -1;

    @FXML
    void click(MouseEvent event) {
        if (!tablero.juegoTerminado() && tablero.getMovimientos() > 0) {
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Salir");
            alerta.setHeaderText("Hay una partida en curso. ¿Deseas salir?");
            alerta.initModality(Modality.APPLICATION_MODAL);
            alerta.initOwner(gridTablero.getScene().getWindow());

            ButtonType btnSi = new ButtonType("Sí, salir");
            ButtonType btnNo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            alerta.getButtonTypes().setAll(btnSi, btnNo);

            Optional<ButtonType> respuesta = alerta.showAndWait();
            if (respuesta.isEmpty() || respuesta.get() != btnSi) return;
        }
        db.disconnect();
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void enter(MouseEvent event) {
        lblCierre.setStyle("-fx-text-fill: red;");
    }

    @FXML
    void exited(MouseEvent event) {
        lblCierre.setStyle("-fx-text-fill: gray;");
    }

    @FXML
    void initialize() {
        tablero        = new Tablero();
        botonesTablero = new Button[3][3];
        simbolo        = "X";
        lblCierre.setStyle("-fx-text-fill: gray;");

        gridTablero.setHgap(10);
        gridTablero.setVgap(10);
        gridTablero.setAlignment(Pos.CENTER);

        crearBotonesTablero();

        Platform.runLater(() -> {
            nombreJugador = pedirNombre();
            lblNombreJugador.setText(nombreJugador);
            lblTurno.setText("Turno: " + nombreJugador);
            inicializarDB();   // <-- se mueve aquí, después del nombre
        });
    }

    private void inicializarDB() {
        db = JSqlite.getInstance();
        db.connect();
        idPartidaActual = crearNuevaPartidaEnBD("Partida sin nombre");
        IO.println("DB inicializada. ID partida actual: " + idPartidaActual);
    }

    private int crearNuevaPartidaEnBD(String nombre) {
        TableroDB t = new TableroDB();
        t.setNombre(nombre);
        t.setFecha(LocalDate.now().toString());
        t.setJugadores((nombreJugador != null ? nombreJugador : "Jugador") + " vs Computadora");
        t.setTableroJson(db.serializarTablero(obtenerCeldasActuales()));
        t.setVictoriasJ1(victoriasJugador);
        t.setVictoriasJ2(victoriasComputadora);
        t.setEmpates(empates);
        t.setTurno(simbolo.equals("X") ? 1 : 2);
        t.setPartidaTerminada(tablero.juegoTerminado());
        return db.create(t);
    }

    private String[][] obtenerCeldasActuales() {
        String[][] celdas = new String[3][3];
        for (int fila = 0; fila < 3; fila++) {
            for (int col = 0; col < 3; col++) {
                String texto = botonesTablero[fila][col].getText();
                celdas[fila][col] = texto != null ? texto : "";
            }
        }
        return celdas;
    }

    private String pedirNombre() {
        String nombre = "";
        while (nombre.isEmpty()) {
            TextInputDialog dialogo = new TextInputDialog();
            dialogo.setTitle("Bienvenido");
            dialogo.setHeaderText("¿Cómo te llamas?");
            dialogo.setContentText("Nombre:");
            dialogo.initModality(Modality.APPLICATION_MODAL);
            dialogo.initOwner(gridTablero.getScene().getWindow());

            Optional<String> resultado = dialogo.showAndWait();

            if (resultado.isPresent()) {
                nombre = resultado.get().trim();
                if (nombre.isEmpty()) {
                    Alert aviso = new Alert(Alert.AlertType.WARNING);
                    aviso.setHeaderText("El nombre no puede estar vacío.");
                    aviso.initOwner(gridTablero.getScene().getWindow());
                    aviso.showAndWait();
                }
            } else {
                nombre = "Jugador";
            }
        }
        return nombre;
    }

    private void crearBotonesTablero() {
        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                Button boton = new Button();
                boton.setFont(Font.font("Arial", FontWeight.BOLD, 40));
                boton.setPrefSize(80, 80);
                boton.setStyle("-fx-background-color: #d9d9d9; -fx-border-color: #bbbbbb; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");

                final int f = fila;
                final int c = columna;
                botonesTablero[fila][columna] = boton;

                boton.setOnAction(actionEvent -> {
                    if (boton.getText().isEmpty() && !tablero.juegoTerminado()) {
                        boton.setText(simbolo);
                        tablero.hacerMovimiento(f, c, simbolo);

                        if (simbolo.equals("X")) {
                            boton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #e94560; -fx-border-color: #bbbbbb; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
                        } else {
                            boton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #1a6ef5; -fx-border-color: #bbbbbb; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
                        }

                        String ganador = tablero.verificarGanador();

                        if (ganador != null) {
                            String quien = ganador.equals("X") ? nombreJugador : "Computadora";
                            if (ganador.equals("X")) {
                                victoriasJugador++;
                                lblMarcadorJugador.setText(String.valueOf(victoriasJugador));
                            } else {
                                victoriasComputadora++;
                                lblMarcadorCompu.setText(String.valueOf(victoriasComputadora));
                            }
                            mostrarResultado("¡" + quien + " ha ganado!");

                        } else if (tablero.esEmpate()) {
                            empates++;
                            lblEmpates.setText("Empates: " + empates);
                            mostrarResultado("¡Empate!");

                        } else {
                            cambiarTurno();
                        }
                    }
                });

                boton.setOnMouseEntered(e -> {
                    if (boton.getText().isEmpty()) {
                        boton.setStyle("-fx-background-color: #c0c0c0; -fx-border-color: #999999; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
                    }
                });

                boton.setOnMouseExited(e -> {
                    if (boton.getText().isEmpty()) {
                        boton.setStyle("-fx-background-color: #d9d9d9; -fx-border-color: #bbbbbb; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
                    }
                });

                gridTablero.add(boton, columna, fila);
            }
        }
    }

    private void mostrarResultado(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.NONE);
        alerta.setTitle("Resultado");
        alerta.setHeaderText(mensaje);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.initOwner(gridTablero.getScene().getWindow());
        alerta.initStyle(StageStyle.UTILITY);

        ButtonType continuar = new ButtonType("Continuar");
        ButtonType salir     = new ButtonType("Salir");
        alerta.getButtonTypes().setAll(continuar, salir);

        Optional<ButtonType> respuesta = alerta.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == salir) {
            db.disconnect();
            Platform.exit();
            System.exit(0);
        } else {
            limpiarTablero();
        }
    }

    public void cambiarTurno() {
        simbolo = simbolo.equals("X") ? "O" : "X";

        if (simbolo.equals("X")) {
            lblTurno.setText("Turno: " + nombreJugador);
            imgJugador.setOpacity(1.0);
            imgComputadora.setOpacity(0.4);
        } else {
            lblTurno.setText("Turno: Computadora");
            imgJugador.setOpacity(0.4);
            imgComputadora.setOpacity(1.0);
        }
    }

    @FXML
    void accionGuardar() {
        TextInputDialog dialogo = new TextInputDialog(
                idPartidaActual == -1 ? "Mi partida" :
                        Optional.ofNullable(db.read(idPartidaActual))
                                .map(TableroDB::getNombre)
                                .orElse("Mi partida")
        );
        dialogo.setTitle("Guardar partida");
        dialogo.setHeaderText("Nombre de la partida:");
        dialogo.initModality(Modality.APPLICATION_MODAL);

        Optional<String> resultado = dialogo.showAndWait();
        if (resultado.isEmpty() || resultado.get().trim().isEmpty()) return;

        String nombre = resultado.get().trim();

        if (idPartidaActual == -1) {
            idPartidaActual = crearNuevaPartidaEnBD(nombre);
        } else {
            TableroDB t = db.read(idPartidaActual);
            if (t == null) {
                idPartidaActual = crearNuevaPartidaEnBD(nombre);
            } else {
                t.setNombre(nombre);
                t.setFecha(LocalDate.now().toString());
                t.setTableroJson(db.serializarTablero(obtenerCeldasActuales()));
                t.setVictoriasJ1(victoriasJugador);
                t.setVictoriasJ2(victoriasComputadora);
                t.setEmpates(empates);
                t.setTurno(simbolo.equals("X") ? 1 : 2);
                t.setPartidaTerminada(tablero.juegoTerminado());
                db.update(t);
            }
        }

        Alert aviso = new Alert(Alert.AlertType.INFORMATION);
        aviso.setHeaderText("Partida guardada: " + nombre);
        aviso.showAndWait();
        IO.println("Partida guardada: " + nombre + " (ID: " + idPartidaActual + ")");
    }

    @FXML
    void accionCargar() {
        Stage ownerStage = (Stage) gridTablero.getScene().getWindow();
        DialogoPartidas dialogo = new DialogoPartidas(ownerStage);
        dialogo.showAndWait();

        TableroDB seleccionada = dialogo.getPartidaSeleccionada();
        if (seleccionada == null) return;

        idPartidaActual = seleccionada.getId();
        String[][] celdas = db.deserializarTablero(seleccionada.getTableroJson());

        for (int fila = 0; fila < 3; fila++) {
            for (int col = 0; col < 3; col++) {
                String texto = (celdas[fila][col] != null) ? celdas[fila][col] : "";
                botonesTablero[fila][col].setText(texto);
                if (texto.equals("X")) {
                    botonesTablero[fila][col].setStyle(
                            "-fx-background-color: #f0f0f0; -fx-text-fill: #e94560; " +
                                    "-fx-border-color: #bbbbbb; -fx-border-width: 1; " +
                                    "-fx-border-radius: 10; -fx-background-radius: 10;");
                } else if (texto.equals("O")) {
                    botonesTablero[fila][col].setStyle(
                            "-fx-background-color: #f0f0f0; -fx-text-fill: #1a6ef5; " +
                                    "-fx-border-color: #bbbbbb; -fx-border-width: 1; " +
                                    "-fx-border-radius: 10; -fx-background-radius: 10;");
                } else {
                    botonesTablero[fila][col].setStyle(
                            "-fx-background-color: #d9d9d9; -fx-border-color: #bbbbbb; " +
                                    "-fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
                }
            }
        }

        victoriasJugador     = seleccionada.getVictoriasJ1();
        victoriasComputadora = seleccionada.getVictoriasJ2();
        empates              = seleccionada.getEmpates();
        simbolo              = seleccionada.getTurno() == 1 ? "X" : "O";

        lblMarcadorJugador.setText(String.valueOf(victoriasJugador));
        lblMarcadorCompu.setText(String.valueOf(victoriasComputadora));
        lblEmpates.setText("Empates: " + empates);
        lblTurno.setText("Turno: " + (simbolo.equals("X") ? nombreJugador : "Computadora"));

        IO.println("Partida cargada: " + seleccionada.getNombre());
    }

    @FXML
    void jugarDeNuevo() {
        if (tablero.getMovimientos() > 0) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Jugar de nuevo");
            confirm.setHeaderText("¿Deseas guardar la partida antes de reiniciar?");
            confirm.initModality(Modality.APPLICATION_MODAL);

            ButtonType btnGuardar = new ButtonType("Guardar");
            ButtonType btnDescartar = new ButtonType("Descartar");
            ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirm.getButtonTypes().setAll(btnGuardar, btnDescartar, btnCancelar);

            Optional<ButtonType> resp = confirm.showAndWait();
            if (resp.isEmpty() || resp.get() == btnCancelar) return;
            if (resp.get() == btnGuardar) accionGuardar();
        }

        idPartidaActual = crearNuevaPartidaEnBD("Partida sin nombre");
        tablero.reiniciarJuego();
        limpiarTablero();
    }

    @FXML
    void volver() {
        tablero.reiniciarJuego();
        simbolo              = "X";
        victoriasJugador     = 0;
        victoriasComputadora = 0;
        empates              = 0;
        lblMarcadorJugador.setText("0");
        lblMarcadorCompu.setText("0");
        lblEmpates.setText("Empates: 0");
        idPartidaActual = crearNuevaPartidaEnBD("Partida sin nombre");
        limpiarTablero();
    }

    private void limpiarTablero() {
        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                botonesTablero[fila][columna].setText("");
                botonesTablero[fila][columna].setStyle(
                        "-fx-background-color: #d9d9d9; -fx-border-color: #bbbbbb; " +
                                "-fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
            }
        }
        simbolo = "X";
        lblTurno.setText("Turno: " + nombreJugador);
        imgJugador.setOpacity(1.0);
        imgComputadora.setOpacity(0.4);
    }
}
