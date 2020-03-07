package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Accion;
import cl.udelvd.modelo.Emoticon;
import cl.udelvd.modelo.Evento;
import cl.udelvd.repositorios.AccionRepositorio;
import cl.udelvd.repositorios.EmoticonRepositorio;
import cl.udelvd.repositorios.EventoRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class EditarEventoViewModel extends AndroidViewModel {

    private AccionRepositorio accionRepositorio;
    private EmoticonRepositorio emoticonRepositorio;
    private EventoRepositorio eventoRepositorio;

    public EditarEventoViewModel(@NonNull Application application) {
        super(application);
    }

    /*
    EVENTO
     */
    public SingleLiveEvent<Evento> cargarEvento(Evento evento) {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        return eventoRepositorio.obtenerEvento(evento);
    }

    public SingleLiveEvent<String> mostrarMsgErrorEvento() {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        return eventoRepositorio.getResponseErrorMsgEvento();
    }

    public MutableLiveData<Boolean> isLoadingEvento() {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        return eventoRepositorio.getIsLoading();
    }

    public SingleLiveEvent<String> mostrarMsgErrorActualizacion() {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        return eventoRepositorio.getResponseErrorMsgActualizacion();
    }

    public SingleLiveEvent<String> mostrarMsgActualizacion() {
        eventoRepositorio = EventoRepositorio.getInstancia(getApplication());
        return eventoRepositorio.getResponseMsgActualizacion();
    }

    /*
    ACCIONES
     */
    public MutableLiveData<Boolean> isLoadingAcciones() {
        accionRepositorio = AccionRepositorio.getInstancia(getApplication());
        return accionRepositorio.getIsLoading();
    }

    public SingleLiveEvent<String> mostrarMsgErrorAcciones() {
        accionRepositorio = AccionRepositorio.getInstancia(getApplication());
        return accionRepositorio.getResponseMsgErrorListado();
    }

    public MutableLiveData<List<Accion>> cargarAcciones(String idioma) {
        accionRepositorio = AccionRepositorio.getInstancia(getApplication());
        return accionRepositorio.obtenerAccionesIdioma(idioma);
    }

    public void refreshAcciones(String idioma) {
        accionRepositorio = AccionRepositorio.getInstancia(getApplication());
        accionRepositorio.obtenerAccionesIdioma(idioma);
    }

    /*
    EMOTICONES
     */

    public MutableLiveData<Boolean> isLoadingEmoticones() {
        emoticonRepositorio = EmoticonRepositorio.getInstancia(getApplication());
        return emoticonRepositorio.getIsLoading();
    }

    public MutableLiveData<List<Emoticon>> cargarEmoticones() {
        emoticonRepositorio = EmoticonRepositorio.getInstancia(getApplication());
        return emoticonRepositorio.obtenerEmoticones();
    }

    public SingleLiveEvent<String> mostrarMsgErrorEmoticones() {
        emoticonRepositorio = EmoticonRepositorio.getInstancia(getApplication());
        return emoticonRepositorio.getResponseMsgErrorListado();
    }

    public void refreshEmoticones() {
        emoticonRepositorio = EmoticonRepositorio.getInstancia(getApplication());
        emoticonRepositorio.obtenerEmoticones();
    }
}
