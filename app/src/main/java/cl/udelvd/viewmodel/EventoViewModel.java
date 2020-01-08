package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Evento;
import cl.udelvd.repositorios.EventoRepositorio;

public class EventoViewModel extends AndroidViewModel {

    private EventoRepositorio repositorio;

    private MutableLiveData<List<Evento>> eventoMutableLiveData;

    public EventoViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<Evento>> cargarEventos(Entrevista entrevista) {

        if (eventoMutableLiveData == null) {
            eventoMutableLiveData = new MutableLiveData<>();
            repositorio = EventoRepositorio.getInstancia(getApplication());
            eventoMutableLiveData = repositorio.obtenerEventosEntrevista(entrevista);
        }
        return eventoMutableLiveData;
    }
}
