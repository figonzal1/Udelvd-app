package cl.udelvd.model;

import java.util.Date;

public class Usuario {

    private int id;
    private String nombre;
    private String apellido;
    private String sexo;

    private Date fechaNacimiento;
    private String ciudad;
    private boolean jubiladoLegal;

    private boolean caidas;
    private int nCaidas;

    private int nConvivientes3Meses;


    private int idInvestigador;
    private int idEstadoCivil;

    //opcionales
    private int idNivelEducacional;
    private int idConviviente;
    private int idProfesion;
    private String createTime;

    public Usuario() {
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

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
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

    public int getnCaidas() {
        return nCaidas;
    }

    public void setnCaidas(int nCaidas) {
        this.nCaidas = nCaidas;
    }

    public int getnConvivientes3Meses() {
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

    public int getIdEstadoCivil() {
        return idEstadoCivil;
    }

    public void setIdEstadoCivil(int idEstadoCivil) {
        this.idEstadoCivil = idEstadoCivil;
    }

    public int getIdNivelEducacional() {
        return idNivelEducacional;
    }

    public void setIdNivelEducacional(int idNivelEducacional) {
        this.idNivelEducacional = idNivelEducacional;
    }

    public int getIdConviviente() {
        return idConviviente;
    }

    public void setIdConviviente(int idConviviente) {
        this.idConviviente = idConviviente;
    }

    public int getIdProfesion() {
        return idProfesion;
    }

    public void setIdProfesion(int idProfesion) {
        this.idProfesion = idProfesion;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", sexo='" + sexo + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", ciudad='" + ciudad + '\'' +
                ", jubiladoLegal=" + jubiladoLegal +
                ", caidas=" + caidas +
                ", nCaidas=" + nCaidas +
                ", nConvivientes3Meses=" + nConvivientes3Meses +
                ", idInvestigador=" + idInvestigador +
                ", idEstadoCivil=" + idEstadoCivil +
                ", idNivelEducacional=" + idNivelEducacional +
                ", idConviviente=" + idConviviente +
                ", idProfesion=" + idProfesion +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
