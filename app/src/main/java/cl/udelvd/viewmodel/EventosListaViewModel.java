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

public class EventosListaViewModel extends AndroidViewModel {

    private EventoRepositorio eventoRepositorio;

    private MutableLiveData<List<Evento>> mutableLiveData;

    public EventosListaViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        return eventoRepositorio.getIsLoading();
    }

    public MutableLiveData<List<Evento>> cargarEventos(Entrevista entrevista) {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
            eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
            mutableLiveData = eventoRepositorio.obtenerEventosEntrevista(entrevista);
        }
        return mutableLiveData;
    }

    public SingleLiveEvent<String> mostrarMsgErrorListado() {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        return eventoRepositorio.getResponseErrorMsgListado();
    }

    public void refreshEventos(Entrevista entrevista) {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        eventoRepositorio.obtenerEventosEntrevista(entrevista);
    }

    public SingleLiveEvent<String> mostrarMsgEliminar() {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        return eventoRepositorio.getResponseMsgEliminar();
    }

    public SingleLiveEvent<String> mostrarMsgErrorEliminar() {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        return eventoRepositorio.getResponseErrorMsgEliminar();
    }
}
