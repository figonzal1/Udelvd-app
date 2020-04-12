package cl.udelvd.modelo;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

public class Evento {

    private int id;
    private Entrevista entrevista;
    private Accion accion;
    private Emoticon emoticon;
    private String justificacion;
    private Date hora_evento;


    public Evento() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Entrevista getEntrevista() {
        return entrevista;
    }

    public void setEntrevista(Entrevista entrevista) {
        this.entrevista = entrevista;
    }

    public Accion getAccion() {
        return accion;
    }

    public void setAccion(Accion accion) {
        this.accion = accion;
    }

    public Emoticon getEmoticon() {
        return emoticon;
    }

    public void setEmoticon(Emoticon emoticon) {
        this.emoticon = emoticon;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public Date getHora_evento() {
        return hora_evento;
    }

    public void setHora_evento(Date hora_evento) {
        this.hora_evento = hora_evento;
    }

    @NonNull
    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", entrevista=" + entrevista +
                ", accion=" + accion +
                ", emoticon=" + emoticon +
                ", justificacion='" + justificacion + '\'' +
                ", hora_evento='" + hora_evento + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evento)) return false;
        Evento evento = (Evento) o;
        return getJustificacion().toLowerCase().equals(evento.getJustificacion().toLowerCase()) &&
                getHora_evento().equals(evento.getHora_evento());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getJustificacion(), getHora_evento());
    }
}
