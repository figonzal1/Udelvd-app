package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Accion;
import cl.udelvd.repositorios.AccionRepositorio;

public class AccionViewModel extends AndroidViewModel {

    private AccionRepositorio repositorio;

    private MutableLiveData<List<Accion>> accionesMutableLiveData;

    public AccionViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Funcion encargada de cargar la lista de acciones desde el repositorio hacia la interfaz mediante ViewMoedel
     *
     * @return MutableLiveData con la lista de acciones
     */
    public MutableLiveData<List<Accion>> cargarAcciones() {
        if (accionesMutableLiveData == null) {
            accionesMutableLiveData = new MutableLiveData<>();
            repositorio = AccionRepositorio.getInstancia(getApplication());
        }
        return repositorio.obtenerAcciones();
    }
}
