package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Ciudad;
import cl.udelvd.repositorios.CiudadRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class CiudadViewModel extends AndroidViewModel {

    private CiudadRepositorio repositorio;

    private MutableLiveData<List<Ciudad>> ciudadMutableLiveData;

    public CiudadViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Funcion encargada de cargar la lista de ciudades a la intefaz mediante ViewModel
     *
     * @return MutableLiveData de ciudades del sistema
     */
    public MutableLiveData<List<Ciudad>> cargarCiudades() {

        if (ciudadMutableLiveData == null) {
            ciudadMutableLiveData = new MutableLiveData<>();
            repositorio = CiudadRepositorio.getInstancia(getApplication());
            ciudadMutableLiveData = repositorio.obtenerCiudades();
        }
        return ciudadMutableLiveData;
    }

    /**
     * Funcion encargada de enviar mensaje de error desde repositorio hacia interfaz de usuario por medio de ViewModel
     *
     * @return SingleLiveEvent
     */
    public SingleLiveEvent<String> mostrarMsgError() {
        repositorio = CiudadRepositorio.getInstancia(getApplication());
        return repositorio.getResponseMsgError();
    }

    public void refreshCiudades() {
        repositorio = CiudadRepositorio.getInstancia(getApplication());
        repositorio.obtenerCiudades();
    }
}
