package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Ciudad;
import cl.udelvd.repositorios.CiudadRepositorio;

public class CiudadViewModel extends AndroidViewModel {

    private CiudadRepositorio ciudadRepositorio;

    public CiudadViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<Ciudad>> cargarCiudades() {

        ciudadRepositorio = CiudadRepositorio.getInstancia(getApplication());
        return ciudadRepositorio.obtenerCiudades();
    }

}
