package cl.udelvd.models;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Action {

    private int id;
    private String name;
    private String nameEs;
    private String nameEng;

    public Action() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameEs() {
        return nameEs;
    }

    public void setNameEs(String nameEs) {
        this.nameEs = nameEs;
    }

    public String getNameEng() {
        return nameEng;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return getNameEs().equalsIgnoreCase(action.getNameEs()) &&
                getNameEng().equalsIgnoreCase(action.getNameEng());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNameEs(), getNameEng());
    }
}
