package cl.udelvd.modelo;

public class Accion {

    private int id;
    private String nombre;

    public Accion() {
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

    @Override
    public String toString() {
        return "Accion{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
