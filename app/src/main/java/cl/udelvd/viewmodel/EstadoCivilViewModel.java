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

    private MutableLiveData<List<EstadoCivil>> estadoCivilMutableLiveData;

    public EstadoCivilViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Funcion encargada de cargar la lista de estados civiles a la interfaz mediante ViewModel
     *
     * @return MutableLiveData de estados civiles
     */
    public MutableLiveData<List<EstadoCivil>> cargarEstadosCiviles() {
        if (estadoCivilMutableLiveData == null) {
            estadoCivilMutableLiveData = new MutableLiveData<>();
            repositorio = EstadoCivilRepositorio.getInstance(getApplication());
            estadoCivilMutableLiveData = repositorio.obtenerEstadosCiviles();
        }
        return estadoCivilMutableLiveData;
    }
}
