package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class RecuperacionViewModel extends AndroidViewModel {

    private InvestigadorRepositorio investigadorRepositorio;

    public RecuperacionViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getIsLoading();
    }

    public SingleLiveEvent<String> mostrarMsgErrorRecuperacion() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsgErrorRecuperacion();

    }

    public SingleLiveEvent<String> mostrarMsgRecuperacon() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsgRecuperacion();
    }
}
