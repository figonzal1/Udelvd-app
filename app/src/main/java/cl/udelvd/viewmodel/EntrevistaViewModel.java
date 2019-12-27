package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.repositorios.EntrevistaRepositorio;

public class EntrevistaViewModel extends AndroidViewModel {

    private EntrevistaRepositorio repositorio;
    private MutableLiveData<List<Entrevista>> entrevistasMutableLiveData;

    public EntrevistaViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<Entrevista>> cargarEntrevistas(Entrevistado entrevistado) {

        if (entrevistasMutableLiveData == null) {
            entrevistasMutableLiveData = new MutableLiveData<>();
            repositorio = EntrevistaRepositorio.getInstancia(getApplication());
            entrevistasMutableLiveData = repositorio.obtenerEntrevistasPersonales(entrevistado);
        }
        return entrevistasMutableLiveData;
    }
}
