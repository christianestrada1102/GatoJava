package modelo.sqlite;

import controller.IO;
import modelo.TableroDB;

import java.sql.*;
import java.util.ArrayList;

public class JSqlite implements Database {

    private final static JSqlite instance = new JSqlite();

    public static JSqlite getInstance() {
        return instance;
    }

    private Connection connection;
    private StringBuilder message;
    private ArrayList<TableroDB> partidas;
    private int lastId;

    private JSqlite() {
        this.lastId = -1;
        this.partidas = new ArrayList<>();
    }

    @Override
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:el_gato.db");
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(Sql.create);
            stmt.close();
            IO.println("Conexión establecida con el_gato.db");
        } catch (ClassNotFoundException e) {
            IO.println("Driver SQLite no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            IO.println("Error al conectar con la BD: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                IO.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            IO.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    @Override
    public int create(TableroDB tablero) {
        int idGenerado = -1;
        try (PreparedStatement ps = connection.prepareStatement(
                Sql.insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tablero.getNombre());
            ps.setString(2, tablero.getFecha());
            ps.setString(3, tablero.getJugadores());
            ps.setString(4, tablero.getTableroJson());
            ps.setInt(5, tablero.getVictoriasJ1());
            ps.setInt(6, tablero.getVictoriasJ2());
            ps.setInt(7, tablero.getEmpates());
            ps.setInt(8, tablero.getTurno());
            ps.setInt(9, tablero.isPartidaTerminada() ? 1 : 0);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
                lastId = idGenerado;
            }
            IO.println("Partida guardada con ID: " + idGenerado);
        } catch (SQLException e) {
            IO.println("Error al guardar partida: " + e.getMessage());
        }
        return idGenerado;
    }

    @Override
    public TableroDB read(int id) {
        try (PreparedStatement ps = connection.prepareStatement(Sql.ls_s)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            IO.println("Error al leer partida ID " + id + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public void update(TableroDB tablero) {
        try (PreparedStatement ps = connection.prepareStatement(Sql.update)) {
            ps.setString(1, tablero.getNombre());
            ps.setString(2, tablero.getFecha());
            ps.setString(3, tablero.getJugadores());
            ps.setString(4, tablero.getTableroJson());
            ps.setInt(5, tablero.getVictoriasJ1());
            ps.setInt(6, tablero.getVictoriasJ2());
            ps.setInt(7, tablero.getEmpates());
            ps.setInt(8, tablero.getTurno());
            ps.setInt(9, tablero.isPartidaTerminada() ? 1 : 0);
            ps.setInt(10, tablero.getId());
            ps.executeUpdate();
            IO.println("Partida actualizada: ID " + tablero.getId());
        } catch (SQLException e) {
            IO.println("Error al actualizar partida: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement ps = connection.prepareStatement(Sql.rm)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            IO.println("Partida eliminada: ID " + id);
        } catch (SQLException e) {
            IO.println("Error al eliminar partida ID " + id + ": " + e.getMessage());
        }
    }

    @Override
    public ArrayList<TableroDB> getPartidas() {
        partidas.clear();
        try (PreparedStatement ps = connection.prepareStatement(Sql.ls);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                partidas.add(mapear(rs));
            }
        } catch (SQLException e) {
            IO.println("Error al obtener partidas: " + e.getMessage());
        }
        return new ArrayList<>(partidas);
    }

    @Override
    public TableroDB getLast() {
        try (PreparedStatement ps = connection.prepareStatement(Sql.ls_last);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            IO.println("Error al obtener última partida: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String serializarTablero(String[][] celdas) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < celdas.length; i++) {
            sb.append("[");
            for (int j = 0; j < celdas[i].length; j++) {
                String val = (celdas[i][j] != null) ? celdas[i][j] : "";
                sb.append("\"").append(val).append("\"");
                if (j < celdas[i].length - 1) sb.append(",");
            }
            sb.append("]");
            if (i < celdas.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String[][] deserializarTablero(String json) {
        String[][] celdas = new String[3][3];
        String s = json.trim();
        s = s.substring(1, s.length() - 1);
        String[] filas = s.split("\\],\\[");
        for (int i = 0; i < filas.length && i < 3; i++) {
            String fila = filas[i].replace("[", "").replace("]", "");
            String[] celdaFila = fila.split(",");
            for (int j = 0; j < celdaFila.length && j < 3; j++) {
                celdas[i][j] = celdaFila[j].replace("\"", "");
            }
        }
        return celdas;
    }

    @Override
    public void setMessage(StringBuilder message) {
        this.message = message;
    }

    @Override
    public void getMessage() {
        IO.println(message != null ? message.toString() : "(sin mensaje)");
    }

    public int getLastId() {
        return lastId;
    }

    private TableroDB mapear(ResultSet rs) throws SQLException {
        return new TableroDB(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("fecha"),
            rs.getString("jugadores"),
            rs.getString("tablero"),
            rs.getInt("victorias_j1"),
            rs.getInt("victorias_j2"),
            rs.getInt("empates"),
            rs.getInt("turno"),
            rs.getInt("partida_terminada") == 1
        );
    }
}
