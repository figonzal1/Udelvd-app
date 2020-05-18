package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.models.Action;
import cl.udelvd.models.Emoticon;
import cl.udelvd.models.Event;
import cl.udelvd.repositories.ActionRepository;
import cl.udelvd.repositories.EmoticonRepository;
import cl.udelvd.repositories.EventRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class EditEventViewModel extends AndroidViewModel {

    private ActionRepository actionRepository;
    private EmoticonRepository emoticonRepository;
    private EventRepository eventRepository;

    public EditEventViewModel(@NonNull Application application) {
        super(application);
    }

    /*
    EVENT
     */
    public SingleLiveEvent<Event> loadEvent(Event event) {
        eventRepository = EventRepository.getInstance(getApplication());
        return eventRepository.getEvent(event);
    }

    public void refreshEvent(Event eventIntent) {
        eventRepository = EventRepository.getInstance(getApplication());
        eventRepository.getEvent(eventIntent);
    }

    public SingleLiveEvent<String> showMsgErrorEvent() {
        eventRepository = EventRepository.getInstance(getApplication());
        return eventRepository.getResponseErrorMsgEvent();
    }

    public MutableLiveData<Boolean> isLoadingEvent() {
        eventRepository = EventRepository.getInstance(getApplication());
        return eventRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorUpdate() {
        eventRepository = EventRepository.getInstance(getApplication());
        return eventRepository.getResponseErrorMsgUpdate();
    }

    public SingleLiveEvent<String> showMsgUpdate() {
        eventRepository = EventRepository.getInstance(getApplication());
        return eventRepository.getResponseMsgUpdate();
    }

    /*
    ACTION
     */
    public MutableLiveData<Boolean> isLoadingAction() {
        actionRepository = ActionRepository.getInstance(getApplication());
        return actionRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorAction() {
        actionRepository = ActionRepository.getInstance(getApplication());
        return actionRepository.getResponseMsgErrorList();
    }

    public MutableLiveData<List<Action>> loadAction(String idioma) {
        actionRepository = ActionRepository.getInstance(getApplication());
        return actionRepository.getActionsByLanguage(idioma);
    }

    public void refreshAction(String idioma) {
        actionRepository = ActionRepository.getInstance(getApplication());
        actionRepository.getActionsByLanguage(idioma);
    }

    /*
    EMOTICONS
     */

    public MutableLiveData<Boolean> isLoadingEmoticons() {
        emoticonRepository = EmoticonRepository.getInstance(getApplication());
        return emoticonRepository.getIsLoading();
    }

    public MutableLiveData<List<Emoticon>> loadEmoticons() {
        emoticonRepository = EmoticonRepository.getInstance(getApplication());
        return emoticonRepository.getEmoticons();
    }

    public SingleLiveEvent<String> showMsgErrorEmoticons() {
        emoticonRepository = EmoticonRepository.getInstance(getApplication());
        return emoticonRepository.getResponseMsgErrorList();
    }

    public void refreshEmoticons() {
        emoticonRepository = EmoticonRepository.getInstance(getApplication());
        emoticonRepository.getEmoticons();
    }
}
