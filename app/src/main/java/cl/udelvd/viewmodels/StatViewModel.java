package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.models.Stat;
import cl.udelvd.repositories.StatsRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class StatViewModel extends AndroidViewModel {

    private StatsRepository repository;
    private MutableLiveData<List<Stat>> mutableLiveData;

    public StatViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        repository = StatsRepository.getInstance(getApplication());
        return repository.getIsLoading();
    }

    public MutableLiveData<List<Stat>> loadStats() {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
            repository = StatsRepository.getInstance(getApplication());
            mutableLiveData = repository.getStats();
        }

        return mutableLiveData;
    }

    public SingleLiveEvent<String> showMsgErrorList() {
        repository = StatsRepository.getInstance(getApplication());
        return repository.getResponseMsgErrorList();
    }

    public void refreshStats() {
        repository = StatsRepository.getInstance(getApplication());
        repository.getStats();
    }
}
