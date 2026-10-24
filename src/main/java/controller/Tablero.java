package controller;

public class Tablero {
    private final String[][] tablero;

    private int movimientos;


    public Tablero() {

        this.tablero = new String[3][3];
        this.movimientos = 0;
        limpiarTablero();
    }

    private void limpiarTablero() {
        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                tablero[fila][columna] = "";
            }
        }
        movimientos = 0;
    }

    public void hacerMovimiento(int fila, int columna, String simbolo) {
        if (fila >= 0 && fila < 3 && columna >= 0 && columna < 3 &&
                tablero[fila][columna].isEmpty()) {
            tablero[fila][columna] = simbolo;
            movimientos++;
        }
    }
    public String verificarGanador() {
        for (int fila = 0; fila < 3; fila++) {
            if (!tablero[fila][0].isEmpty() &&
                    tablero[fila][0].equals(tablero[fila][1]) &&
                    tablero[fila][0].equals(tablero[fila][2])) {
                return tablero[fila][0];
            }
        }


        for (int columna = 0; columna < 3; columna++) {
            if (!tablero[0][columna].isEmpty() &&
                    tablero[0][columna].equals(tablero[1][columna]) &&
                    tablero[0][columna].equals(tablero[2][columna])) {
                return tablero[0][columna];
            }
        }

        if (!tablero[0][0].isEmpty() &&
                tablero[0][0].equals(tablero[1][1]) &&
                tablero[0][0].equals(tablero[2][2])) {
            return tablero[0][0];
        }

        if (!tablero[0][2].isEmpty() &&
                tablero[0][2].equals(tablero[1][1]) &&
                tablero[0][2].equals(tablero[2][0])) {
            return tablero[0][2];
        }

        return null;
    }

    public boolean esEmpate() {
        return movimientos == 9 && verificarGanador() == null;
    }

    public boolean juegoTerminado() {
        return verificarGanador() != null || esEmpate();
    }

    public void reiniciarJuego() {
        limpiarTablero();

    }

    public String[][] getTablero() {
        return tablero;
    }

    public int getMovimientos() {
        return movimientos;
    }


}
