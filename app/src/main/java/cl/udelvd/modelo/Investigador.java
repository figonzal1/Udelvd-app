package cl.udelvd.modelo;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Investigador {

    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private boolean activado;

    private int idRol;
    private String nombreRol;

    private String createTime;

    public Investigador() {
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public boolean isActivado() {
        return activado;
    }

    public void setActivado(boolean activado) {
        this.activado = activado;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "Investigador{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", idRol=" + idRol +
                ", nombreRol='" + nombreRol + '\'' +
                ", activado=" + activado +
                ", createTime=" + createTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Investigador that = (Investigador) o;
        return getNombre().equals(that.getNombre()) &&
                getApellido().equals(that.getApellido()) &&
                getEmail().equals(that.getEmail()) &&
                Objects.equals(getNombreRol(), that.getNombreRol());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNombre(), getApellido(), getEmail(), getIdRol(), getNombreRol(), isActivado());
    }
}
