package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.models.Action;
import cl.udelvd.models.Emoticon;
import cl.udelvd.repositories.ActionRepository;
import cl.udelvd.repositories.EmoticonRepository;
import cl.udelvd.repositories.EventRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class NewEventViewModel extends AndroidViewModel {

    private EventRepository eventRepository;
    private ActionRepository actionRepository;
    private EmoticonRepository emoticonRepository;

    public NewEventViewModel(@NonNull Application application) {
        super(application);
    }

    /*
    EVENTS
     */
    public MutableLiveData<Boolean> isLoadingEvents() {
        eventRepository = EventRepository.getInstance(getApplication());
        return eventRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorRegistry() {
        eventRepository = EventRepository.getInstance(getApplication());
        return eventRepository.getResponseErrorMsgRegistry();
    }

    public SingleLiveEvent<String> showMsgRegistry() {
        eventRepository = EventRepository.getInstance(getApplication());
        return eventRepository.getResponseMsgRegistry();
    }

    /*
    ACTIONS
     */
    public MutableLiveData<Boolean> isLoadingActions() {
        actionRepository = ActionRepository.getInstance(getApplication());
        return actionRepository.getIsLoading();
    }

    public SingleLiveEvent<String> showMsgErrorActions() {
        actionRepository = ActionRepository.getInstance(getApplication());
        return actionRepository.getResponseMsgErrorList();
    }

    public MutableLiveData<List<Action>> loadActions(String idioma) {
        actionRepository = ActionRepository.getInstance(getApplication());
        return actionRepository.getActionsByLanguage(idioma);
    }

    public void refreshActions(String idioma) {
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
