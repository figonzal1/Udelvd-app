package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.TipoEntrevista;
import cl.udelvd.repositorios.TipoEntrevistaRepositorio;

public class TipoEntrevistaViewModel extends AndroidViewModel {

    private TipoEntrevistaRepositorio repositorio;

    private MutableLiveData<List<TipoEntrevista>> tipoEntrevistaMutableLiveData;

    public TipoEntrevistaViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Funcion encargada de cargar la lista de tipos de entrevista a la interfaz mediante ViewModel
     *
     * @return MutableLiveData de tipos de entrevistas en el sistema
     */
    public MutableLiveData<List<TipoEntrevista>> cargarTiposEntrevistas() {
        if (tipoEntrevistaMutableLiveData == null) {
            tipoEntrevistaMutableLiveData = new MutableLiveData<>();
            repositorio = TipoEntrevistaRepositorio.getInstancia(getApplication());
            tipoEntrevistaMutableLiveData = repositorio.obtenerTiposEntrevista();
        }
        return tipoEntrevistaMutableLiveData;
    }
}
