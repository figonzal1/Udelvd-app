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

    /**
     * Funcion que envia el mensaje de error desde el respositorio a la interfaz
     * (Con observer)
     *
     * @return SingleLive con mensaje de error
     */
    public SingleLiveEvent<String> mostrarErrorRespuesta() {
        repositorio = EventoRepositorio.getInstancia(getApplication());
        return repositorio.getResponseErrorMsg();
    }

    /**
     * Funcion que envia el mensaje de registro exitoso desde el respositorio a la interfaz
     * (Con observer)
     *
     * @return SingleLive con mensaje de error
     */
    public SingleLiveEvent<String> mostrarRespuestaRegistro() {
        repositorio = EventoRepositorio.getInstancia(getApplication());
        return repositorio.getResponseMsgRegistro();
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
}
