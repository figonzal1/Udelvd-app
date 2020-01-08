package cl.udelvd.modelo;

public class Evento {

    private int id;
    private Entrevista entrevista;
    private Accion accion;
    private Emoticon emoticon;
    private String justificacion;
    private String hora_evento;

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

    public String getHora_evento() {
        return hora_evento;
    }

    public void setHora_evento(String hora_evento) {
        this.hora_evento = hora_evento;
    }

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
}
