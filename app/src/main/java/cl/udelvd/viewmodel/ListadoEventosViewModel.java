package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Evento;
import cl.udelvd.repositorios.EventoRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class ListadoEventosViewModel extends AndroidViewModel {

    private EventoRepositorio eventoRepositorio;

    public ListadoEventosViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        return eventoRepositorio.getIsLoading();
    }

    public MutableLiveData<List<Evento>> cargarEventos(Entrevista entrevista) {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        return eventoRepositorio.obtenerEventosEntrevista(entrevista);
    }

    public SingleLiveEvent<String> mostrarMsgErrorListado() {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        return eventoRepositorio.getResponseErrorMsgListado();
    }

    public void refreshEventos(Entrevista entrevista) {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        eventoRepositorio.obtenerEventosEntrevista(entrevista);
    }
}
