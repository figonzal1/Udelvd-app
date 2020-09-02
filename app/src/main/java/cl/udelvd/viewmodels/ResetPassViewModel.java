package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import cl.udelvd.repositories.ResearcherRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class ResetPassViewModel extends AndroidViewModel {

    private ResearcherRepository researcherRepository;

    public ResetPassViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorReset() {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResponseMsgErrorReset();

    }

    public SingleLiveEvent<String> showMsgReset() {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResponseMsgReset();
    }
}
