package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Map;

import cl.udelvd.repositories.ResearcherRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class RecoveryViewModel extends AndroidViewModel {

    private ResearcherRepository researcherRepository;

    public RecoveryViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorRecovery() {
        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResponseMsgErrorRecovery();

    }

    public SingleLiveEvent<Map<String, String>> showMsgRecovery() {
        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResponseMsgRecovery();
    }
}
