package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.models.Researcher;
import cl.udelvd.repositories.ResearcherRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class ResearcherListViewModel extends AndroidViewModel {

    private ResearcherRepository researcherRepository;

    public ResearcherListViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getIsLoading();
    }

    public MutableLiveData<Boolean> activatingResearcher() {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.activatingResearcher();
    }

    public SingleLiveEvent<List<Researcher>> loadFirstPage(int page, Researcher researcher) {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResearchers(page, researcher);
    }

    public void loadNextPage(int page, Researcher researcher) {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        researcherRepository.getResearchers(page, researcher);
    }

    public SingleLiveEvent<List<Researcher>> showNextPage() {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getNextPage();
    }

    public SingleLiveEvent<String> showMsgErrorList() {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResponseMsgErrorList();
    }

    public SingleLiveEvent<String> showMsgActivation() {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResponseMsgActivation();
    }

    public SingleLiveEvent<String> showMsgErrorActivation() {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResponseMsgErrorActivation();
    }

    public MutableLiveData<Integer> showNInterviewees() {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getNResearcher();
    }

    public void refreshResearchers(Researcher admin) {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        researcherRepository.getResearchers(1, admin);
    }
}
