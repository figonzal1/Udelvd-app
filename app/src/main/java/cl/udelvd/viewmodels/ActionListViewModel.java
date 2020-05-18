package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.models.Action;
import cl.udelvd.repositories.ActionRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class ActionListViewModel extends AndroidViewModel {

    private ActionRepository repository;
    private MutableLiveData<List<Action>> mutableLiveData;

    public ActionListViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        repository = ActionRepository.getInstance(getApplication());
        return repository.getIsLoading();
    }

    public MutableLiveData<List<Action>> loadActions() {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
            repository = ActionRepository.getInstance(getApplication());
            mutableLiveData = repository.getActions();
        }
        return mutableLiveData;
    }

    public SingleLiveEvent<String> showMsgErrorList() {
        repository = ActionRepository.getInstance(getApplication());
        return repository.getResponseMsgErrorList();
    }

    public void refreshActions() {
        repository = ActionRepository.getInstance(getApplication());
        repository.getActions();
    }

    public SingleLiveEvent<String> showMsgErrorDelete() {
        repository = ActionRepository.getInstance(getApplication());
        return repository.getResponseMsgErrorDelete();
    }

    public SingleLiveEvent<String> showMsgDelete() {
        repository = ActionRepository.getInstance(getApplication());
        return repository.getResponseMsgDelete();
    }
}
