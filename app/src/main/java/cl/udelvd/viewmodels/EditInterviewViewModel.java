package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.models.Interview;
import cl.udelvd.models.InterviewType;
import cl.udelvd.repositories.InterviewRepository;
import cl.udelvd.repositories.InterviewTypeRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class EditInterviewViewModel extends AndroidViewModel {

    private InterviewRepository interviewRepository;
    private InterviewTypeRepository interviewTypeRepository;

    private MutableLiveData<List<InterviewType>> interviewTypeMutableLiveData;

    public EditInterviewViewModel(@NonNull Application application) {
        super(application);
    }

    /*
    INTERVIEW TYPES
     */
    public MutableLiveData<List<InterviewType>> loadInterviewTypes() {
        if (interviewTypeMutableLiveData == null) {
            interviewTypeMutableLiveData = new MutableLiveData<>();
            interviewTypeRepository = InterviewTypeRepository.getInstance(getApplication());
            interviewTypeMutableLiveData = interviewTypeRepository.getInterviewType();
        }
        return interviewTypeMutableLiveData;
    }

    public SingleLiveEvent<String> showMsgErrorInterviewTypeList() {
        interviewTypeRepository = InterviewTypeRepository.getInstance(getApplication());
        return interviewTypeRepository.getResponseMsgErrorList();
    }

    public MutableLiveData<Boolean> isLoadingInterviewTypes() {
        interviewTypeRepository = InterviewTypeRepository.getInstance(getApplication());
        return interviewTypeRepository.getIsLoading();
    }

    public void refreshInterviewTypes() {
        interviewTypeRepository = InterviewTypeRepository.getInstance(getApplication());
        interviewTypeRepository.getInterviewType();
    }

    /*
    INTERVIEW
     */
    public SingleLiveEvent<Interview> loadInterview(Interview interview) {
        interviewRepository = InterviewRepository.getInstance(getApplication());
        return interviewRepository.getPersonalInterview(interview);
    }

    public MutableLiveData<Boolean> isLoadingInterview() {
        interviewRepository = InterviewRepository.getInstance(getApplication());
        return interviewRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorInterview() {
        interviewRepository = InterviewRepository.getInstance(getApplication());
        return interviewRepository.getResponseMsgErrorInterview();
    }

    public SingleLiveEvent<String> showMsgUpdate() {
        interviewRepository = InterviewRepository.getInstance(getApplication());
        return interviewRepository.getResponseMsgUpdate();
    }

    public SingleLiveEvent<String> showMsgErrorUpdate() {
        interviewRepository = InterviewRepository.getInstance(getApplication());
        return interviewRepository.getResponseMsgErrorUpdate();
    }

    public void refreshInterview(Interview interviewIntent) {
        interviewRepository = InterviewRepository.getInstance(getApplication());
        interviewRepository.getPersonalInterview(interviewIntent);
    }
}
