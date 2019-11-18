package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utils.SingleLiveEvent;

public class InvestigadorViewModel extends AndroidViewModel {

    private InvestigadorRepositorio investigadorRepositorio;

    public InvestigadorViewModel(@NonNull Application application) {
        super(application);
    }

    public SingleLiveEvent<String> mostrarMsgRespuesta() {

        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsg();

    }

    public SingleLiveEvent<String> mostrarErrorRespuesta() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getErrorMsg();
    }
}
