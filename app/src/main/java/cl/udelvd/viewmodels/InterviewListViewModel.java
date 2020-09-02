package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.models.Interview;
import cl.udelvd.models.Interviewee;
import cl.udelvd.repositories.InterviewRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class InterviewListViewModel extends AndroidViewModel {

    private InterviewRepository interviewRepository;

    public InterviewListViewModel(@NonNull Application application) {
        super(application);
    }

    public SingleLiveEvent<List<Interview>> loadInterviews(Interviewee interviewee) {

        interviewRepository = InterviewRepository.getInstance(getApplication());
        return interviewRepository.getPersonalInterviews(interviewee);
    }

    public void refreshInterviews(Interviewee interviewee) {

        interviewRepository = InterviewRepository.getInstance(getApplication());
        interviewRepository.getPersonalInterviews(interviewee);
    }

    public SingleLiveEvent<String> showMsgErrorList() {

        interviewRepository = InterviewRepository.getInstance(getApplication());
        return interviewRepository.getResponseMsgErrorList();
    }

    public MutableLiveData<Boolean> isLoading() {

        interviewRepository = InterviewRepository.getInstance(getApplication());
        return interviewRepository.getIsLoading();
    }

    public SingleLiveEvent<String> mostrarMsgDelete() {

        interviewRepository = InterviewRepository.getInstance(getApplication());
        return interviewRepository.getResponseMsgDelete();
    }

    public SingleLiveEvent<String> showMsgErrorDelete() {

        interviewRepository = InterviewRepository.getInstance(getApplication());
        return interviewRepository.getResponseMsgErrorDelete();
    }
}
