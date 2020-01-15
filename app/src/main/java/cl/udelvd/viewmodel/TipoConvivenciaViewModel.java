package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.TipoConvivencia;
import cl.udelvd.repositorios.TipoConvivenciaRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class TipoConvivenciaViewModel extends AndroidViewModel {

    private TipoConvivenciaRepositorio repositorio;

    private MutableLiveData<List<TipoConvivencia>> tipoConvivenciaMutableLiveData;

    public TipoConvivenciaViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Funcion encargada de carar la lista de tipos de convivencia a la interfaz mediante ViewModel
     *
     * @return MutableLiveData de tipos de convivencia en el sistema
     */
    public MutableLiveData<List<TipoConvivencia>> cargarTiposConvivencias() {

        if (tipoConvivenciaMutableLiveData == null) {
            tipoConvivenciaMutableLiveData = new MutableLiveData<>();
            repositorio = TipoConvivenciaRepositorio.getInstancia(getApplication());
            tipoConvivenciaMutableLiveData = repositorio.obtenerTiposConvivencias();
        }

        return tipoConvivenciaMutableLiveData;
    }

    public SingleLiveEvent<String> mostrarMsgError() {
        repositorio = TipoConvivenciaRepositorio.getInstancia(getApplication());
        return repositorio.getResponseMsgError();
    }

    public void refreshTipoConvivencia() {
        repositorio = TipoConvivenciaRepositorio.getInstancia(getApplication());
        repositorio.obtenerTiposConvivencias();
    }
}
