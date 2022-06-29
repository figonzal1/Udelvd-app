package cl.udelvd.models;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

public class Event {

    private int id;
    private Interview interview;
    private Action action;
    private Emoticon emoticon;
    private String justification;
    private Date eventHour;


    public Event() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Emoticon getEmoticon() {
        return emoticon;
    }

    public void setEmoticon(Emoticon emoticon) {
        this.emoticon = emoticon;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Date getEventHour() {
        return eventHour;
    }

    public void setEventHour(Date eventHour) {
        this.eventHour = eventHour;
    }

    @NonNull
    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", interview=" + interview +
                ", action=" + action +
                ", emoticon=" + emoticon +
                ", justification='" + justification + '\'' +
                ", eventHour='" + eventHour + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return getJustification().equalsIgnoreCase(event.getJustification()) &&
                getEventHour().equals(event.getEventHour());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getJustification(), getEventHour());
    }
}
