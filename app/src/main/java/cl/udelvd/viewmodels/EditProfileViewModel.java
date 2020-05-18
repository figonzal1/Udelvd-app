package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Map;

import cl.udelvd.repositories.ResearcherRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class EditProfileViewModel extends AndroidViewModel {

    private ResearcherRepository researcherRepository;

    public EditProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getIsLoading();
    }

    public SingleLiveEvent<Map<String, Object>> showMsgUpdate() {
        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResponseMsgUpdate();
    }

    public SingleLiveEvent<String> showMsgErrorUpdate() {
        researcherRepository = ResearcherRepository.getInstance(getApplication());
        return researcherRepository.getResponseMsgErrorUpdate();
    }
}
