package cl.udelvd.modelo;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

public class Entrevistado {

    private int id;
    private String nombre;
    private String apellido;
    private String sexo;

    private Date fechaNacimiento;
    private boolean jubiladoLegal;

    private boolean caidas;
    private int nCaidas;

    private int nConvivientes3Meses;

    private int idInvestigador;
    private Ciudad ciudad;
    private EstadoCivil estadoCivil;

    //opcionales
    private NivelEducacional nivelEducacional;
    private TipoConvivencia tipoConvivencia;
    private Profesion profesion;

    //Relaciones
    private int n_entrevistas; //Numero total de entrevistas de la persona
    private String nombre_investigador;
    private String apellido_investigador;

    public Entrevistado() {
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

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public boolean isJubiladoLegal() {
        return jubiladoLegal;
    }

    public void setJubiladoLegal(boolean jubiladoLegal) {
        this.jubiladoLegal = jubiladoLegal;
    }

    public boolean isCaidas() {
        return caidas;
    }

    public void setCaidas(boolean caidas) {
        this.caidas = caidas;
    }

    public int getNCaidas() {
        return nCaidas;
    }

    public void setNCaidas(int nCaidas) {
        this.nCaidas = nCaidas;
    }

    public int getNConvivientes3Meses() {
        return nConvivientes3Meses;
    }

    public void setnConvivientes3Meses(int nConvivientes3Meses) {
        this.nConvivientes3Meses = nConvivientes3Meses;
    }

    public int getIdInvestigador() {
        return idInvestigador;
    }

    public void setIdInvestigador(int idInvestigador) {
        this.idInvestigador = idInvestigador;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public NivelEducacional getNivelEducacional() {
        return nivelEducacional;
    }

    public void setNivelEducacional(NivelEducacional nivelEducacional) {
        this.nivelEducacional = nivelEducacional;
    }

    public TipoConvivencia getTipoConvivencia() {
        return tipoConvivencia;
    }

    public void setTipoConvivencia(TipoConvivencia tipoConvivencia) {
        this.tipoConvivencia = tipoConvivencia;
    }

    public Profesion getProfesion() {
        return profesion;
    }

    public void setProfesion(Profesion profesion) {
        this.profesion = profesion;
    }

    public int getN_entrevistas() {
        return n_entrevistas;
    }

    public void setN_entrevistas(int n_entrevistas) {
        this.n_entrevistas = n_entrevistas;
    }

    public String getNombre_investigador() {
        return nombre_investigador;
    }

    public void setNombre_investigador(String nombre_investigador) {
        this.nombre_investigador = nombre_investigador;
    }

    public String getApellido_investigador() {
        return apellido_investigador;
    }

    public void setApellido_investigador(String apellido_investigador) {
        this.apellido_investigador = apellido_investigador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entrevistado that = (Entrevistado) o;
        return isJubiladoLegal() == that.isJubiladoLegal() &&
                isCaidas() == that.isCaidas() &&
                nConvivientes3Meses == that.nConvivientes3Meses &&
                getIdInvestigador() == that.getIdInvestigador() &&
                getNombre().equals(that.getNombre()) &&
                getApellido().equals(that.getApellido()) &&
                getSexo().equals(that.getSexo()) &&
                getFechaNacimiento().equals(that.getFechaNacimiento());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNombre(), getApellido(), getSexo(), getFechaNacimiento(), isJubiladoLegal(), isCaidas(), nConvivientes3Meses, getIdInvestigador(), getCiudad(), getEstadoCivil());
    }

    @NonNull
    @Override
    public String toString() {
        return "Entrevistado{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", sexo='" + sexo + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", jubiladoLegal=" + jubiladoLegal +
                ", caidas=" + caidas +
                ", nCaidas=" + nCaidas +
                ", nConvivientes3Meses=" + nConvivientes3Meses +
                ", idInvestigador=" + idInvestigador +
                ", ciudad=" + ciudad +
                ", estadoCivil=" + estadoCivil +
                ", nivelEducacional=" + nivelEducacional +
                ", tipoConvivencia=" + tipoConvivencia +
                ", profesion=" + profesion +
                '}';
    }


}
