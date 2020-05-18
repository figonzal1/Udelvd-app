package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.models.City;
import cl.udelvd.models.CivilState;
import cl.udelvd.models.CoexistanceType;
import cl.udelvd.models.EducationalLevel;
import cl.udelvd.models.Interviewee;
import cl.udelvd.models.Profession;
import cl.udelvd.repositories.CityRepository;
import cl.udelvd.repositories.CivilStateRepository;
import cl.udelvd.repositories.CoexistenceTypeRepository;
import cl.udelvd.repositories.EducationaLevelRepository;
import cl.udelvd.repositories.IntervieweeRepository;
import cl.udelvd.repositories.ProfessionRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class EditIntervieweeViewModel extends AndroidViewModel {

    private CityRepository cityRepository;
    private CivilStateRepository civilStateRepository;
    private EducationaLevelRepository educationaLevelRepository;
    private CoexistenceTypeRepository coexistenceTypeRepository;
    private ProfessionRepository professionRepository;

    private IntervieweeRepository intervieweeRepository;

    private MutableLiveData<List<City>> cityMutableList;
    private MutableLiveData<List<CivilState>> civilStateMutableList;
    private MutableLiveData<List<EducationalLevel>> educationalLevelMutableList;
    private MutableLiveData<List<CoexistanceType>> coexistenceTypeMutableList;
    private MutableLiveData<List<Profession>> professionMutableList;

    public EditIntervieweeViewModel(@NonNull Application application) {
        super(application);
    }

    /*
    INTERVIEWEE
     */
    public SingleLiveEvent<Interviewee> loadInterviewee(Interviewee interviewee) {
        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getInterviewee(interviewee);
    }

    public SingleLiveEvent<String> showMsgErrorInterviewee() {
        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getResponseMsgErrorInterviewee();
    }

    public SingleLiveEvent<String> showMsgUpdate() {
        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getResponseMsgUpdate();
    }

    public SingleLiveEvent<String> showMsgErrorUpdate() {
        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getResponseMsgErrorUpdate();
    }

    public MutableLiveData<Boolean> isLoadingInterviewee() {
        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getIsLoading();
    }

    public void refreshInterviewee(Interviewee intervieweeIntent) {
        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        intervieweeRepository.getInterviewee(intervieweeIntent);
    }

    /*
    CITIES
     */
    public MutableLiveData<List<City>> loadCities() {

        if (cityMutableList == null) {
            cityMutableList = new MutableLiveData<>();
            cityRepository = CityRepository.getInstance(getApplication());
            cityMutableList = cityRepository.getCities();
        }
        return cityMutableList;
    }

    public SingleLiveEvent<String> showMsgErrorCityList() {
        cityRepository = CityRepository.getInstance(getApplication());
        return cityRepository.getResponseMsgErrorList();
    }

    public MutableLiveData<Boolean> isLoadingCities() {
        cityRepository = CityRepository.getInstance(getApplication());
        return cityRepository.getIsLoading();
    }

    public void refreshCities() {
        cityRepository = CityRepository.getInstance(getApplication());
        cityRepository.getCities();
    }

    /*
    CIVIL STATE
     */
    public MutableLiveData<List<CivilState>> loadCivilState() {

        if (civilStateMutableList == null) {
            civilStateMutableList = new MutableLiveData<>();
            civilStateRepository = CivilStateRepository.getInstance(getApplication());
            civilStateMutableList = civilStateRepository.getCivilStates();
        }

        return civilStateMutableList;
    }

    public MutableLiveData<Boolean> isLoadingCivilState() {
        civilStateRepository = CivilStateRepository.getInstance(getApplication());
        return civilStateRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorListCivilState() {
        civilStateRepository = CivilStateRepository.getInstance(getApplication());
        return civilStateRepository.getResponseMsgErrorList();
    }

    public void refreshCivilState() {
        civilStateRepository = CivilStateRepository.getInstance(getApplication());
        civilStateRepository.getCivilStates();
    }

    /*
    EDUCATIONAL LEVEL
     */
    public MutableLiveData<List<EducationalLevel>> loadEducationalLevel() {

        if (educationalLevelMutableList == null) {
            educationalLevelMutableList = new MutableLiveData<>();
            educationaLevelRepository = EducationaLevelRepository.getInstance(getApplication());
            educationalLevelMutableList = educationaLevelRepository.getEducationalLevel();
        }

        return educationalLevelMutableList;
    }

    public MutableLiveData<Boolean> isLoadingEducationalLevel() {
        educationaLevelRepository = EducationaLevelRepository.getInstance(getApplication());
        return educationaLevelRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorEducationalLevel() {
        educationaLevelRepository = EducationaLevelRepository.getInstance(getApplication());
        return educationaLevelRepository.getResponseMsgErrorList();
    }

    public void refreshEducationalLevel() {
        educationaLevelRepository = EducationaLevelRepository.getInstance(getApplication());
        educationaLevelRepository.getEducationalLevel();
    }

    /*
    COEXISTENCE TYPE
     */
    public MutableLiveData<List<CoexistanceType>> loadCoexitenceType() {

        if (coexistenceTypeMutableList == null) {
            coexistenceTypeMutableList = new MutableLiveData<>();
            coexistenceTypeRepository = CoexistenceTypeRepository.getInstance(getApplication());
            coexistenceTypeMutableList = coexistenceTypeRepository.getCoexistenceType();
        }

        return coexistenceTypeMutableList;
    }

    public MutableLiveData<Boolean> isLoadingCoexistenceType() {
        coexistenceTypeRepository = CoexistenceTypeRepository.getInstance(getApplication());
        return coexistenceTypeRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorListCoexistenceType() {
        coexistenceTypeRepository = CoexistenceTypeRepository.getInstance(getApplication());
        return coexistenceTypeRepository.getResponseMsgErrorList();
    }

    public void refreshCoexistenceType() {
        coexistenceTypeRepository = CoexistenceTypeRepository.getInstance(getApplication());
        coexistenceTypeRepository.getCoexistenceType();
    }

    /*
    PROFESSION
     */
    public MutableLiveData<List<Profession>> loadProfession() {

        if (professionMutableList == null) {

            professionMutableList = new MutableLiveData<>();
            professionRepository = ProfessionRepository.getInstance(getApplication());
            professionMutableList = professionRepository.getProfessions();
        }
        return professionMutableList;
    }

    public MutableLiveData<Boolean> isLoadingProfessions() {
        professionRepository = ProfessionRepository.getInstance(getApplication());
        return professionRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorListProfessions() {
        professionRepository = ProfessionRepository.getInstance(getApplication());
        return professionRepository.getResponseMsgErrorList();
    }

    public void refreshProfession() {
        professionRepository = ProfessionRepository.getInstance(getApplication());
        professionRepository.getProfessions();
    }
}
