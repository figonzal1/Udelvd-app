package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import cl.udelvd.repositories.ActionRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class EditActionViewModel extends AndroidViewModel {

    private ActionRepository repository;

    public EditActionViewModel(@NonNull Application application) {
        super(application);
    }

    public SingleLiveEvent<String> showMsgUpdate() {
        repository = ActionRepository.getInstance(getApplication());
        return repository.getResponseMsgUpdate();
    }

    public SingleLiveEvent<String> showMsgErrorUpdate() {
        repository = ActionRepository.getInstance(getApplication());
        return repository.getResponseMsgErrorUpdate();
    }

    public MutableLiveData<Boolean> isLoadingUpdate() {
        repository = ActionRepository.getInstance(getApplication());
        return repository.getIsLoading();
    }
}
