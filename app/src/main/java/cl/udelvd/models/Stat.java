package cl.udelvd.models;

import androidx.annotation.NonNull;

public class Stat {

    private int id;
    private String name;
    private String url;
    private String pin_pass;

    public Stat() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPin_pass() {
        return pin_pass;
    }

    public void setPin_pass(String pin_pass) {
        this.pin_pass = pin_pass;
    }

    @NonNull
    @Override
    public String toString() {
        return "Stat{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", pin_pass='" + pin_pass + '\'' +
                '}';
    }
}
