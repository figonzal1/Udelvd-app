package cl.udelvd.model;

public class Usuario {

    private String nombre;
    private String apellido;
    private int edad;
    private String sexo;
    private String ciudad;
    private int id_investigador;

    public Usuario() {
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

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public int getId_investigador() {
        return id_investigador;
    }

    public void setId_investigador(int id_investigador) {
        this.id_investigador = id_investigador;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", edad=" + edad +
                ", sexo='" + sexo + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", id_investigador=" + id_investigador +
                '}';
    }
}
