package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.Map;

import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utils.SingleLiveEvent;

public class InvestigadorViewModel extends AndroidViewModel {

    private InvestigadorRepositorio investigadorRepositorio;

    public InvestigadorViewModel(@NonNull Application application) {
        super(application);
    }

    public SingleLiveEvent<String> mostrarMsgRespuestaRegistro() {

        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsgRegistro();

    }

    public SingleLiveEvent<Map<String, Object>> mostrarMsgRespuestaLogin() {

        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsgLogin();
    }


    public SingleLiveEvent<String> mostrarErrorRespuesta() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getErrorMsg();
    }
}
