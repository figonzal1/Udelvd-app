package cl.udelvd.models;

import androidx.annotation.NonNull;

public class Emoticon {

    private int id;
    private String url;
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return "Emoticon{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
