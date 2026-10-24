package modelo.sqlite;

import modelo.TableroDB;
import java.util.ArrayList;

public interface Database {

    void connect();
    void disconnect();

    int create(TableroDB tablero);
    TableroDB read(int id);
    void update(TableroDB tablero);
    void delete(int id);

    ArrayList<TableroDB> getPartidas();
    TableroDB getLast();

    String serializarTablero(String[][] celdas);
    String[][] deserializarTablero(String json);

    void setMessage(StringBuilder message);
    void getMessage();
}
