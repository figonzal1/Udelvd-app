package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.models.InterviewType;
import cl.udelvd.repositories.InterviewRepository;
import cl.udelvd.repositories.InterviewTypeRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class NewInterviewViewModel extends AndroidViewModel {

    private InterviewTypeRepository interviewTypeRepository;
    private InterviewRepository interviewRepository;

    private MutableLiveData<List<InterviewType>> interviewTypeMutable;

    public NewInterviewViewModel(@NonNull Application application) {
        super(application);
    }

    /*
    INTERVIEW TYPES
     */
    public MutableLiveData<List<InterviewType>> loadInterviewTypes() {
        if (interviewTypeMutable == null) {
            interviewTypeMutable = new MutableLiveData<>();
            interviewTypeRepository = InterviewTypeRepository.getInstance(getApplication());
            interviewTypeMutable = interviewTypeRepository.getInterviewType();
        }
        return interviewTypeMutable;
    }

    public void refreshInterviewTypes() {
        interviewTypeRepository = InterviewTypeRepository.getInstance(getApplication());
        interviewTypeRepository.getInterviewType();
    }

    public SingleLiveEvent<String> showMsgErrorInterviewTypes() {
        interviewTypeRepository = InterviewTypeRepository.getInstance(getApplication());
        return interviewTypeRepository.getResponseMsgErrorList();
    }

    public MutableLiveData<Boolean> isLoadingInterviewTypes() {
        interviewTypeRepository = InterviewTypeRepository.getInstance(getApplication());
        return interviewTypeRepository.getIsLoading();
    }


    /*
        INTERVIEWEES REGISTRY
     */
    public SingleLiveEvent<String> showMsgErrorRegistry() {
        interviewRepository = InterviewRepository.getInstance(getApplication());
        return interviewRepository.getResponseMsgErrorRegistry();
    }

    public SingleLiveEvent<String> showMsgRegistry() {
        interviewRepository = InterviewRepository.getInstance(getApplication());
        return interviewRepository.getResponseMsgRegistry();
    }

    public MutableLiveData<Boolean> isLoadingRegistryInterviews() {
        interviewRepository = InterviewRepository.getInstance(getApplication());
        return interviewRepository.getIsLoading();
    }
}
