package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Accion;
import cl.udelvd.repositorios.AccionRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class AccionesListViewModel extends AndroidViewModel {

    private AccionRepositorio repositorio;
    private MutableLiveData<List<Accion>> mutableLiveData;

    public AccionesListViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        repositorio = AccionRepositorio.getInstancia(getApplication());
        return repositorio.getIsLoading();
    }

    public MutableLiveData<List<Accion>> cargarAcciones() {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
            repositorio = AccionRepositorio.getInstancia(getApplication());
            mutableLiveData = repositorio.obtenerAcciones();
        }
        return mutableLiveData;
    }

    public SingleLiveEvent<String> mostrarMsgErrorListado() {
        repositorio = AccionRepositorio.getInstancia(getApplication());
        return repositorio.getResponseMsgErrorListado();
    }

    public void refreshAcciones() {
        repositorio = AccionRepositorio.getInstancia(getApplication());
        repositorio.obtenerAcciones();
    }

    /*
    ELIMINAR
     */
    public SingleLiveEvent<String> mostrarMsgErrorEliminar() {
        repositorio = AccionRepositorio.getInstancia(getApplication());
        return repositorio.getResponseMsgErrorEliminar();
    }

    public SingleLiveEvent<String> mostrarMsgEliminar() {
        repositorio = AccionRepositorio.getInstancia(getApplication());
        return repositorio.getResponseMsgEliminar();
    }
}
