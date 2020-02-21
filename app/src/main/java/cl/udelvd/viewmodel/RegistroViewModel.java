package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Map;

import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class RegistroViewModel extends AndroidViewModel {

    private InvestigadorRepositorio investigadorRepositorio;

    public RegistroViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getIsLoading();
    }

    public SingleLiveEvent<Map<String, String>> mostrarMsgRegistro() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsgRegistro();
    }

    public SingleLiveEvent<String> mostrarMsgErrorRegistro() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsgErrorRegistro();
    }
}
