package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import cl.udelvd.repositorios.AccionRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class EditarAccionViewModel extends AndroidViewModel {

    private AccionRepositorio repositorio;

    public EditarAccionViewModel(@NonNull Application application) {
        super(application);
    }

    public SingleLiveEvent<String> mostrarMsgActualizacion() {
        repositorio = AccionRepositorio.getInstancia(getApplication());
        return repositorio.getResponseMsgActualizacion();
    }

    public SingleLiveEvent<String> mostrarMsgErrorActualizacion() {
        repositorio = AccionRepositorio.getInstancia(getApplication());
        return repositorio.getResponseMsgErrorActualizacion();
    }

    public MutableLiveData<Boolean> isLoadingActualizacion() {
        repositorio = AccionRepositorio.getInstancia(getApplication());
        return repositorio.getIsLoading();
    }
}
