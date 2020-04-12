package cl.udelvd.modelo;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Accion {

    private int id;
    private String nombre;
    private String nombreEs;
    private String nombreEn;

    public Accion() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreEs() {
        return nombreEs;
    }

    public void setNombreEs(String nombreEs) {
        this.nombreEs = nombreEs;
    }

    public String getNombreEn() {
        return nombreEn;
    }

    public void setNombreEn(String nombreEn) {
        this.nombreEn = nombreEn;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accion accion = (Accion) o;
        return getNombreEs().toLowerCase().equals(accion.getNombreEs().toLowerCase()) &&
                getNombreEn().toLowerCase().equals(accion.getNombreEn().toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNombreEs(), getNombreEn());
    }
}
