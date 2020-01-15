package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Profesion;
import cl.udelvd.repositorios.ProfesionRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class ProfesionViewModel extends AndroidViewModel {

    private ProfesionRepositorio repositorio;
    private MutableLiveData<List<Profesion>> profesionMutableLiveData;


    public ProfesionViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Funcion encargada de cargar la lista de profesiones a la interfaz mediante ViewModel
     *
     * @return MutableLiveData de profesiones del sistema
     */
    public MutableLiveData<List<Profesion>> cargarProfesiones() {

        if (profesionMutableLiveData == null) {
            profesionMutableLiveData = new MutableLiveData<>();
            repositorio = ProfesionRepositorio.getInstancia(getApplication());
            profesionMutableLiveData = repositorio.obtenerNivelesEducacionales();
        }

        return profesionMutableLiveData;
    }

    /**
     * Funcion encargada de enviar mensaje de error desde repositorio hacia la UI por medio de ViewModel
     *
     * @return SingleLiveData
     */
    public SingleLiveEvent<String> mostrarMsgError() {
        repositorio = ProfesionRepositorio.getInstancia(getApplication());
        return repositorio.getResponseMsgError();
    }
}
