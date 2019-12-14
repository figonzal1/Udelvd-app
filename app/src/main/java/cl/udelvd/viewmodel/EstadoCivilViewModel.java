package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.EstadoCivil;
import cl.udelvd.repositorios.EstadoCivilRepositorio;

public class EstadoCivilViewModel extends AndroidViewModel {

    private EstadoCivilRepositorio repositorio;

    public EstadoCivilViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<EstadoCivil>> cargarEstadosCiviles() {
        repositorio = EstadoCivilRepositorio.getInstance(getApplication());
        return repositorio.obtenerEstadosCiviles();
    }
}
