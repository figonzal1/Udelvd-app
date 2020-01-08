package cl.udelvd.modelo;

public class Emoticon {

    private int id;
    private String url;
    private String descripcion;

    public Emoticon() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Emoticon{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
