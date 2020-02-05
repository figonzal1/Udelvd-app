package cl.udelvd.modelo;

import androidx.annotation.NonNull;

public class TipoEntrevista {

    private int id;
    private String nombre;

    public TipoEntrevista() {
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

    @NonNull
    @Override
    public String toString() {
        return nombre;
    }
}
