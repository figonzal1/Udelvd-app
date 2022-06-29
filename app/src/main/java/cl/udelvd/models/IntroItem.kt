package cl.udelvd.models;

import androidx.annotation.NonNull;

public class IntroItem {

    private final String title;
    private final String description;
    private final int idResource;

    public IntroItem(String title, String description, int idResource) {
        this.title = title;
        this.description = description;
        this.idResource = idResource;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIdResource() {
        return idResource;
    }

    @NonNull
    @Override
    public String toString() {
        return "IntroItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", idResource=" + idResource +
                '}';
    }
}
