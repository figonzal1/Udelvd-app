package cl.udelvd.models;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

public class Interview {

    private int id;
    private int idInterviewee;
    private InterviewType interviewType;
    private Date interviewDate;

    public Interview() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdInterviewee() {
        return idInterviewee;
    }

    public void setIdInterviewee(int idInterviewee) {
        this.idInterviewee = idInterviewee;
    }

    public InterviewType getInterviewType() {
        return interviewType;
    }

    public void setInterviewType(InterviewType interviewType) {
        this.interviewType = interviewType;
    }

    public Date getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(Date interviewDate) {
        this.interviewDate = interviewDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interview)) return false;
        Interview that = (Interview) o;
        return getIdInterviewee() == that.getIdInterviewee() &&
                getInterviewDate().equals(that.getInterviewDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdInterviewee(), getInterviewDate());
    }

    @NonNull
    @Override
    public String toString() {
        return "Interview{" +
                "id=" + id +
                ", idInterviewee=" + idInterviewee +
                ", interviewType=" + interviewType +
                ", interviewDate=" + interviewDate +
                '}';
    }
}
