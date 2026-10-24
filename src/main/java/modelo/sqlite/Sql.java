package modelo.sqlite;

public interface Sql {

    String create = "CREATE TABLE IF NOT EXISTS partida ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "nombre TEXT NOT NULL, "
            + "fecha TEXT NOT NULL, "
            + "jugadores TEXT NOT NULL, "
            + "tablero TEXT NOT NULL, "
            + "victorias_j1 INTEGER NOT NULL DEFAULT 0, "
            + "victorias_j2 INTEGER NOT NULL DEFAULT 0, "
            + "empates INTEGER NOT NULL DEFAULT 0, "
            + "turno INTEGER NOT NULL DEFAULT 1, "
            + "partida_terminada INTEGER NOT NULL DEFAULT 0)";

    String insert = "INSERT INTO partida (nombre, fecha, jugadores, tablero, "
            + "victorias_j1, victorias_j2, empates, turno, partida_terminada) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    String ls = "SELECT * FROM partida ORDER BY id DESC";

    String ls_s = "SELECT * FROM partida WHERE id = ?";

    String ls_last = "SELECT * FROM partida ORDER BY id DESC LIMIT 1";

    String update = "UPDATE partida SET nombre = ?, fecha = ?, jugadores = ?, tablero = ?, "
            + "victorias_j1 = ?, victorias_j2 = ?, empates = ?, turno = ?, partida_terminada = ? "
            + "WHERE id = ?";

    String rm = "DELETE FROM partida WHERE id = ?";
}
