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
import cl.udelvd.models.Profession;
import cl.udelvd.repositories.CityRepository;
import cl.udelvd.repositories.CivilStateRepository;
import cl.udelvd.repositories.CoexistenceTypeRepository;
import cl.udelvd.repositories.EducationaLevelRepository;
import cl.udelvd.repositories.IntervieweeRepository;
import cl.udelvd.repositories.ProfessionRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class NewIntervieweeViewModel extends AndroidViewModel {

    private CityRepository cityRepository;
    private CivilStateRepository civilStateRepository;
    private EducationaLevelRepository educationaLevelRepository;
    private CoexistenceTypeRepository coexistenceTypeRepository;
    private ProfessionRepository professionRepository;

    private IntervieweeRepository intervieweeRepository;

    private MutableLiveData<List<City>> cityMutableList;
    private MutableLiveData<List<CivilState>> civilStateMutableList;
    private MutableLiveData<List<EducationalLevel>> educacionalLevelMutableList;
    private MutableLiveData<List<CoexistanceType>> coexistenceTypeMutableList;
    private MutableLiveData<List<Profession>> professionMutableList;

    public NewIntervieweeViewModel(@NonNull Application application) {
        super(application);
    }

    /*
    INTERVIEWEE
     */
    public SingleLiveEvent<String> showMsgRegistry() {
        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getResponseMsgRegistry();
    }

    public SingleLiveEvent<String> showMsgErrorRegistry() {
        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getResponseMsgErrorRegistry();
    }

    public MutableLiveData<Boolean> isLoadingInterviewee() {
        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getIsLoading();
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

    public SingleLiveEvent<String> showMsgErrorListCity() {
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
    public MutableLiveData<List<CivilState>> loadCivilStates() {
        if (civilStateMutableList == null) {
            civilStateMutableList = new MutableLiveData<>();
            civilStateRepository = CivilStateRepository.getInstance(getApplication());
            civilStateMutableList = civilStateRepository.getCivilStates();
        }
        return civilStateMutableList;
    }

    public MutableLiveData<Boolean> isLoadingCivilStates() {
        civilStateRepository = CivilStateRepository.getInstance(getApplication());
        return civilStateRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorListCivilStates() {
        civilStateRepository = CivilStateRepository.getInstance(getApplication());
        return civilStateRepository.getResponseMsgErrorList();
    }

    public void refreshCivilStates() {
        civilStateRepository = CivilStateRepository.getInstance(getApplication());
        civilStateRepository.getCivilStates();
    }

    /*
    EDUCATIONAL LEVEL
     */
    public MutableLiveData<List<EducationalLevel>> loadEducationalLevels() {

        if (educacionalLevelMutableList == null) {
            educacionalLevelMutableList = new MutableLiveData<>();
            educationaLevelRepository = EducationaLevelRepository.getInstance(getApplication());
            educacionalLevelMutableList = educationaLevelRepository.getEducationalLevel();

        }
        return educacionalLevelMutableList;
    }

    public MutableLiveData<Boolean> isLoadingEducationalLevels() {
        educationaLevelRepository = EducationaLevelRepository.getInstance(getApplication());
        return educationaLevelRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorListEducationalLevels() {
        educationaLevelRepository = EducationaLevelRepository.getInstance(getApplication());
        return educationaLevelRepository.getResponseMsgErrorList();
    }

    public void refreshEducationalLevels() {
        educationaLevelRepository = EducationaLevelRepository.getInstance(getApplication());
        educationaLevelRepository.getEducationalLevel();
    }

    /*
    COEXISTENCE TYPE
     */
    public MutableLiveData<List<CoexistanceType>> loadCoexistenceTypes() {

        if (coexistenceTypeMutableList == null) {
            coexistenceTypeMutableList = new MutableLiveData<>();
            coexistenceTypeRepository = CoexistenceTypeRepository.getInstance(getApplication());
            coexistenceTypeMutableList = coexistenceTypeRepository.getCoexistenceType();
        }
        return coexistenceTypeMutableList;
    }

    public MutableLiveData<Boolean> isLoadingCoexistenceTypes() {
        coexistenceTypeRepository = CoexistenceTypeRepository.getInstance(getApplication());
        return coexistenceTypeRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorListCoexistenceTypes() {
        coexistenceTypeRepository = CoexistenceTypeRepository.getInstance(getApplication());
        return coexistenceTypeRepository.getResponseMsgErrorList();
    }

    public void refreshCoexistenceTypes() {
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

    public MutableLiveData<Boolean> isLoadingProfession() {
        professionRepository = ProfessionRepository.getInstance(getApplication());
        return professionRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorListProfession() {
        professionRepository = ProfessionRepository.getInstance(getApplication());
        return professionRepository.getResponseMsgErrorList();
    }

    public void refreshProfession() {
        professionRepository = ProfessionRepository.getInstance(getApplication());
        professionRepository.getProfessions();
    }
}
