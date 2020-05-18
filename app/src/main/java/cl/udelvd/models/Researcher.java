package cl.udelvd.models;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Researcher {

    private int id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private boolean isActivated;

    private int idRole;
    private String rolName;

    private String createTime;

    public Researcher() {
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        this.isActivated = activated;
    }

    public String getRolName() {
        return rolName;
    }

    public void setRolName(String rolName) {
        this.rolName = rolName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "Researcher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", idRole=" + idRole +
                ", rolName='" + rolName + '\'' +
                ", isActivated=" + isActivated +
                ", createTime=" + createTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Researcher that = (Researcher) o;
        return getName().toLowerCase().equals(that.getName().toLowerCase()) &&
                getLastName().toLowerCase().equals(that.getLastName().toLowerCase()) &&
                getEmail().equals(that.getEmail()) &&
                Objects.equals(getRolName(), that.getRolName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getLastName(), getEmail(), getIdRole(), getRolName(), isActivated());
    }
}
