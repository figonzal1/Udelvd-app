package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import cl.udelvd.repositorios.AccionRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class NuevaAccionViewModel extends AndroidViewModel {

    private AccionRepositorio accionRepositorio;

    public NuevaAccionViewModel(@NonNull Application application) {
        super(application);
    }

    public SingleLiveEvent<String> mostrarMsgRegistro() {
        accionRepositorio = AccionRepositorio.getInstancia(getApplication());
        return accionRepositorio.getResponseMsgRegistro();
    }

    public SingleLiveEvent<String> mostrarMsgErrorRegistro() {
        accionRepositorio = AccionRepositorio.getInstancia(getApplication());
        return accionRepositorio.getResponseMsgErrorRegistro();
    }

    public MutableLiveData<Boolean> isLoadingRegistro() {
        accionRepositorio = AccionRepositorio.getInstancia(getApplication());
        return accionRepositorio.getIsLoading();
    }
}
