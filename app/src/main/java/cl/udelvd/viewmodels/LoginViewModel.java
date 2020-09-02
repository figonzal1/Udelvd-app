package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Map;

import cl.udelvd.repositories.ResearcherRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class LoginViewModel extends AndroidViewModel {

    private ResearcherRepository researcherRepository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public SingleLiveEvent<Map<String, Object>> showMsgLogin() {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResponseMsgLogin();
    }

    public SingleLiveEvent<String> showMsgErrorLogin() {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResponseMsgErrorLogin();
    }

    public MutableLiveData<Boolean> isLoading() {

        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getIsLoading();
    }
}
