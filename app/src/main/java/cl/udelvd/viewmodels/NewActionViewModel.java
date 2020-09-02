package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import cl.udelvd.repositories.ActionRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class NewActionViewModel extends AndroidViewModel {

    private ActionRepository actionRepository;

    public NewActionViewModel(@NonNull Application application) {
        super(application);
    }

    public SingleLiveEvent<String> showMsgRegistry() {

        actionRepository = ActionRepository.getInstance(getApplication());
        return actionRepository.getResponseMsgRegistry();
    }

    public SingleLiveEvent<String> showMsgErrorRegistry() {

        actionRepository = ActionRepository.getInstance(getApplication());
        return actionRepository.getResponseMsgErrorRegistry();
    }

    public MutableLiveData<Boolean> isLoadingRegistry() {

        actionRepository = ActionRepository.getInstance(getApplication());
        return actionRepository.getIsLoading();
    }
}
