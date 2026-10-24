package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TableroTest {

    private Tablero tablero;

    @BeforeEach
    void setUp() {
        tablero = new Tablero();
    }

    @Test
    @DisplayName("El tablero inicia con todas las celdas vacías")
    void testTableroInicia_Vacio() {
        String[][] celdas = tablero.getTablero();
        for (String[] fila : celdas) {
            for (String celda : fila) {
                assertEquals("", celda);
            }
        }
        assertEquals(0, tablero.getMovimientos());
    }

    @Test
    @DisplayName("Detecta ganador cuando hay tres en fila horizontal")
    void testVerificarGanador_FilaHorizontal() {
        tablero.hacerMovimiento(0, 0, "X");
        tablero.hacerMovimiento(0, 1, "X");
        tablero.hacerMovimiento(0, 2, "X");
        assertEquals("X", tablero.verificarGanador());
        assertTrue(tablero.juegoTerminado());
    }

    @Test
    @DisplayName("Una celda ocupada no permite sobreescribirse")
    void testCeldaOcupada_NoSobreescribe() {
        tablero.hacerMovimiento(1, 1, "X");
        tablero.hacerMovimiento(1, 1, "O");
        assertEquals("X", tablero.getTablero()[1][1]);
        assertEquals(1, tablero.getMovimientos());
    }
}