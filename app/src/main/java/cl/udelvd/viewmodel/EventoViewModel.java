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

    public SingleLiveEvent<Evento> cargarEvento(Evento eventoIntent) {
        repositorio = EventoRepositorio.getInstancia(getApplication());
        return repositorio.obtenerEvento(eventoIntent);
    }

    /**
     * Funcion que envia el mensaje de error desde el respositorio a la interfaz
     * (Con observer)
     *
     * @return SingleLive con mensaje de error
     */
    public SingleLiveEvent<String> mostrarRespuestaRegistro() {
        repositorio = EventoRepositorio.getInstancia(getApplication());
        return repositorio.getResponseMsgRegistro();
    }

    /**
     * Funcion que envia el mensaje de error desde el respositorio a la interfaz
     * (Con observer)
     *
     * @return SingleLive con mensaje de error
     */
    public SingleLiveEvent<String> mostrarErrorRegistro() {
        repositorio = EventoRepositorio.getInstancia(getApplication());
        return repositorio.getResponseErrorMsgRegistro();
    }

    /**
     * Funcion que envia el mensaje de error desde el respositorio a la interfaz
     * (Con observer)
     *
     * @return SingleLive con mensaje de error
     */
    public SingleLiveEvent<String> mostrarErrorListado() {
        repositorio = EventoRepositorio.getInstancia(getApplication());
        return repositorio.getResponseErrorMsgListado();
    }

    /**
     * Funcion que envia el mensaje de error desde el respositorio a la interfaz
     * (Con observer)
     *
     * @return SingleLive con mensaje de error
     */
    public SingleLiveEvent<String> mostrarErrorEvento() {
        repositorio = EventoRepositorio.getInstancia(getApplication());
        return repositorio.getResponseErrorMsgEvento();
    }

    /**
     * Funcion encargada de forzar refresh de listado de Eventos
     *
     * @param entrevista Datos necesarios para buscar eventos
     */
    public void refreshEventos(Entrevista entrevista) {
        repositorio = EventoRepositorio.getInstancia(getApplication());
        repositorio.obtenerEventosEntrevista(entrevista);
    }

    /**
     * Funcion para forzar refresh de una evento especifico
     *
     * @param evento Informacion necesaria para refresh
     */
    public void refreshEvento(Evento evento) {
        repositorio = EventoRepositorio.getInstancia(getApplication());
        repositorio.obtenerEvento(evento);
    }
}
