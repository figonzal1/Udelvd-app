package cl.udelvd.modelo;

import java.util.Date;
import java.util.Objects;

public class Entrevista {

    private int id;
    private int id_entrevistado;
    private TipoEntrevista tipoEntrevista;
    private Date fecha_entrevista;

    public Entrevista() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_entrevistado() {
        return id_entrevistado;
    }

    public void setId_entrevistado(int id_entrevistado) {
        this.id_entrevistado = id_entrevistado;
    }

    public TipoEntrevista getTipoEntrevista() {
        return tipoEntrevista;
    }

    public void setTipoEntrevista(TipoEntrevista tipoEntrevista) {
        this.tipoEntrevista = tipoEntrevista;
    }

    public Date getFecha_entrevista() {
        return fecha_entrevista;
    }

    public void setFecha_entrevista(Date fecha_entrevista) {
        this.fecha_entrevista = fecha_entrevista;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entrevista)) return false;
        Entrevista that = (Entrevista) o;
        return getId_entrevistado() == that.getId_entrevistado() &&
                getTipoEntrevista().equals(that.getTipoEntrevista()) &&
                getFecha_entrevista().equals(that.getFecha_entrevista());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId_entrevistado(), getTipoEntrevista(), getFecha_entrevista());
    }
}
