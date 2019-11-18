package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import cl.udelvd.repositorios.InvestigadorRepositorio;

public class InvestigadorViewModel extends AndroidViewModel {

    private InvestigadorRepositorio investigadorRepositorio;

    public InvestigadorViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> mostrarMsgRespuesta() {

        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsg();

    }

    public MutableLiveData<String> mostrarErrorRespuesta() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getErrorMsg();
    }
}
