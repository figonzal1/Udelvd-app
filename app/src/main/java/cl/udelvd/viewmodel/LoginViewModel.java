package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Map;

import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class LoginViewModel extends AndroidViewModel {

    private InvestigadorRepositorio investigadorRepositorio;

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public SingleLiveEvent<Map<String, Object>> mostrarMsgLogin() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsgLogin();
    }

    public SingleLiveEvent<String> mostrarMsgErrorLogin() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsgErrorLogin();
    }

    public MutableLiveData<Boolean> isLoading() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getIsLoading();
    }
}
