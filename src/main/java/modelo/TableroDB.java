package modelo;

public class TableroDB {

    private int id;
    private String nombre;
    private String fecha;
    private String jugadores;
    private String tableroJson;
    private int victoriasJ1;
    private int victoriasJ2;
    private int empates;
    private int turno;
    private boolean partidaTerminada;

    public TableroDB() {}

    public TableroDB(int id, String nombre, String fecha, String jugadores,
                     String tableroJson, int victoriasJ1, int victoriasJ2,
                     int empates, int turno, boolean partidaTerminada) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.jugadores = jugadores;
        this.tableroJson = tableroJson;
        this.victoriasJ1 = victoriasJ1;
        this.victoriasJ2 = victoriasJ2;
        this.empates = empates;
        this.turno = turno;
        this.partidaTerminada = partidaTerminada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getJugadores() {
        return jugadores;
    }

    public void setJugadores(String jugadores) {
        this.jugadores = jugadores;
    }

    public String getTableroJson() {
        return tableroJson;
    }

    public void setTableroJson(String tableroJson) {
        this.tableroJson = tableroJson;
    }

    public int getVictoriasJ1() {
        return victoriasJ1;
    }

    public void setVictoriasJ1(int victoriasJ1) {
        this.victoriasJ1 = victoriasJ1;
    }

    public int getVictoriasJ2() {
        return victoriasJ2;
    }

    public void setVictoriasJ2(int victoriasJ2) {
        this.victoriasJ2 = victoriasJ2;
    }

    public int getEmpates() {
        return empates;
    }

    public void setEmpates(int empates) {
        this.empates = empates;
    }

    public int getTurno() {
        return turno;
    }

    public void setTurno(int turno) {
        this.turno = turno;
    }

    public boolean isPartidaTerminada() {
        return partidaTerminada;
    }

    public void setPartidaTerminada(boolean partidaTerminada) {
        this.partidaTerminada = partidaTerminada;
    }

    @Override
    public String toString() {
        return "TableroDB{id=" + id + ", nombre='" + nombre + "', fecha='" + fecha + "'}";
    }
}
