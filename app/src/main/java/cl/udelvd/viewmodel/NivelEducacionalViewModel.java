package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.NivelEducacional;
import cl.udelvd.repositorios.NivelEducacionalRepositorio;

public class NivelEducacionalViewModel extends AndroidViewModel {

    private NivelEducacionalRepositorio repositorio;
    private MutableLiveData<List<NivelEducacional>> nivelEducMutableLiveData;

    public NivelEducacionalViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Funcion encargada de carar la lista de niveles educacionales a la interfaz meiante ViewModel
     *
     * @return MutableLiveData de niveles educacionales del sistema
     */
    public MutableLiveData<List<NivelEducacional>> cargarNivelesEduc() {

        if (nivelEducMutableLiveData == null) {
            nivelEducMutableLiveData = new MutableLiveData<>();
            repositorio = NivelEducacionalRepositorio.getInstancia(getApplication());
            nivelEducMutableLiveData = repositorio.obtenerNivelesEducacionales();
        }

        return nivelEducMutableLiveData;
    }
}
