package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.models.Event;
import cl.udelvd.models.Interview;
import cl.udelvd.repositories.EventRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class EventListViewModel extends AndroidViewModel {

    private EventRepository eventRepository;

    public EventListViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {

        eventRepository = EventRepository.getInstance(getApplication());
        return eventRepository.getIsLoading();
    }

    public SingleLiveEvent<List<Event>> loadEvents(Interview interview) {

        eventRepository = EventRepository.getInstance(getApplication());
        return eventRepository.getInterviewEvents(interview);
    }

    public SingleLiveEvent<String> showMsgErrorList() {

        eventRepository = EventRepository.getInstance(getApplication());
        return eventRepository.getResponseErrorMsgList();
    }

    public void refreshEvents(Interview interview) {

        eventRepository = EventRepository.getInstance(getApplication());
        eventRepository.getInterviewEvents(interview);
    }

    public SingleLiveEvent<String> showMsgDelete() {

        eventRepository = EventRepository.getInstance(getApplication());
        return eventRepository.getResponseMsgDelete();
    }

    public SingleLiveEvent<String> showMsgErrorDelete() {

        eventRepository = EventRepository.getInstance(getApplication());
        return eventRepository.getResponseErrorMsgDelete();
    }
}
