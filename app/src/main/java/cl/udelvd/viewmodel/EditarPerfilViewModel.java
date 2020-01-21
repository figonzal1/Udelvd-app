package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Map;

import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class EditarPerfilViewModel extends AndroidViewModel {

    private InvestigadorRepositorio investigadorRepositorio;

    public EditarPerfilViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getIsLoading();
    }

    public SingleLiveEvent<Map<String, Object>> mostrarMsgActualizacion() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsgActualizacion();
    }

    public SingleLiveEvent<String> mostrarMsgErrorActualizacion() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsgErrorActualizacion();
    }
}
