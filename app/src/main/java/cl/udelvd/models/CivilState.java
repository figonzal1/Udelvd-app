package cl.udelvd.models;

import androidx.annotation.NonNull;

public class CivilState {

    private int id;
    private String name;

    public CivilState() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
