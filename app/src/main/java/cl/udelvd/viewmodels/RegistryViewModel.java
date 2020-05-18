package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Map;

import cl.udelvd.repositories.ResearcherRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class RegistryViewModel extends AndroidViewModel {

    private ResearcherRepository researcherRepository;

    public RegistryViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getIsLoading();
    }

    public SingleLiveEvent<Map<String, String>> showMsgRegistry() {
        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResponseMsgRegistry();
    }

    public SingleLiveEvent<String> showMsgErrorRegistry() {
        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResponseMsgErrorRegistry();
    }
}
