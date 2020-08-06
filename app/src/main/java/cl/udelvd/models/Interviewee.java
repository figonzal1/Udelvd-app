package cl.udelvd.models;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

public class Interviewee {

    private int id;
    private String name;
    private String lastName;
    private String gender;

    private Date birthDate;
    private boolean legalRetired;

    private boolean isFalls;
    private int nFalls;

    private int nCohabiting3Months;

    private int idResearcher;
    private City city;
    private CivilState civilState;

    //opcionales
    private EducationalLevel educationalLevel;
    private CohabitType coexistenteType;
    private Profession profession;

    //Relaciones
    private int nInterviews; //Numero total de entrevistas de la persona
    private String researcherName;
    private String lastNameResearcher;

    public Interviewee() {
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public boolean isLegalRetired() {
        return legalRetired;
    }

    public void setLegalRetired(boolean legalRetired) {
        this.legalRetired = legalRetired;
    }

    public boolean isFalls() {
        return isFalls;
    }

    public void setFalls(boolean falls) {
        this.isFalls = falls;
    }

    public int getNCaidas() {
        return nFalls;
    }

    public void setNCaidas(int nCaidas) {
        this.nFalls = nCaidas;
    }

    public int getNConvivientes3Meses() {
        return nCohabiting3Months;
    }

    public void setnCohabiting3Months(int nCohabiting3Months) {
        this.nCohabiting3Months = nCohabiting3Months;
    }

    public int getIdResearcher() {
        return idResearcher;
    }

    public void setIdResearcher(int idResearcher) {
        this.idResearcher = idResearcher;
    }

    public CivilState getCivilState() {
        return civilState;
    }

    public void setCivilState(CivilState civilState) {
        this.civilState = civilState;
    }

    public EducationalLevel getEducationalLevel() {
        return educationalLevel;
    }

    public void setEducationalLevel(EducationalLevel educationalLevel) {
        this.educationalLevel = educationalLevel;
    }

    public CohabitType getCoexistenteType() {
        return coexistenteType;
    }

    public void setCoexistenteType(CohabitType coexistenteType) {
        this.coexistenteType = coexistenteType;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public int getnInterviews() {
        return nInterviews;
    }

    public void setnInterviews(int nInterviews) {
        this.nInterviews = nInterviews;
    }

    public String getResearcherName() {
        return researcherName;
    }

    public void setResearcherName(String researcherName) {
        this.researcherName = researcherName;
    }

    public String getLastNameResearcher() {
        return lastNameResearcher;
    }

    public void setLastNameResearcher(String lastNameResearcher) {
        this.lastNameResearcher = lastNameResearcher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interviewee that = (Interviewee) o;
        return isLegalRetired() == that.isLegalRetired() &&
                isFalls() == that.isFalls() &&
                nCohabiting3Months == that.nCohabiting3Months &&
                getIdResearcher() == that.getIdResearcher() &&
                getName().toLowerCase().equals(that.getName().toLowerCase()) &&
                getLastName().toLowerCase().equals(that.getLastName().toLowerCase()) &&
                getGender().toLowerCase().equals(that.getGender().toLowerCase()) &&
                getBirthDate().equals(that.getBirthDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getLastName(), getGender(), getBirthDate(), isLegalRetired(), isFalls(), nCohabiting3Months, getIdResearcher(), getCity(), getCivilState());
    }

    @NonNull
    @Override
    public String toString() {
        return "Interviewee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", genre='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", legalRetired=" + legalRetired +
                ", isFalls=" + isFalls +
                ", nFalls=" + nFalls +
                ", nCohabiting3Months=" + nCohabiting3Months +
                ", idResearcher=" + idResearcher +
                ", city=" + city +
                ", civilState=" + civilState +
                ", educationalLevel=" + educationalLevel +
                ", coexistenteType=" + coexistenteType +
                ", profession=" + profession +
                '}';
    }


}
