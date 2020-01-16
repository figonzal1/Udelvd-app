package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.repositorios.EntrevistaRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class EntrevistaViewModel extends AndroidViewModel {

    private EntrevistaRepositorio repositorio;
    private MutableLiveData<List<Entrevista>> entrevistasMutableLiveData;

    public EntrevistaViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Funcion encargada de mostrar el listado de entrevistas específicas
     *
     * @param entrevistado Datos del entrevistado para consultar sus entrevistas
     * @return Lista mutable con entrevistas
     */
    public MutableLiveData<List<Entrevista>> cargarEntrevistas(Entrevistado entrevistado) {

        if (entrevistasMutableLiveData == null) {
            entrevistasMutableLiveData = new MutableLiveData<>();
            repositorio = EntrevistaRepositorio.getInstancia(getApplication());
            entrevistasMutableLiveData = repositorio.obtenerEntrevistasPersonales(entrevistado);
        }
        return entrevistasMutableLiveData;
    }

    /**
     * Funcion encargada de refrescar listado de usuarios desde repositorio al viewmodel
     *
     * @param entrevistado Objeto entrevistado usado para entrevistas asociadas
     */
    public void refreshEntrevistas(Entrevistado entrevistado) {
        repositorio = EntrevistaRepositorio.getInstancia(getApplication());
        repositorio.obtenerEntrevistasPersonales(entrevistado);
    }

    /**
     * Funcion encargada de solicitar datos de una entrevista y entregarlos al formulario de UI
     *
     * @param entrevista Datos de la entrevista pra ser buscada
     * @return Mutable con el objeto entrevista
     */
    public SingleLiveEvent<Entrevista> cargarEntrevista(Entrevista entrevista) {
        repositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return repositorio.obtenerEntrevistaPersonal(entrevista);
    }

    /**
     * Funcion encargada de mostrar la respuesta post registro de entrevista en la UI
     *
     * @return SingleLiveEvent con evento unico de mensaje
     */
    public SingleLiveEvent<String> mostrarRespuestaRegistro() {
        repositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return repositorio.getResponseMsgRegistro();
    }

    /**
     * Funcion encargada de mostrar la respuesta erronea post registro de entrevista en la UI
     *
     * @return SingleLiveEvent con evento unico de mensaje
     */
    public SingleLiveEvent<String> mostrarRespuestaError() {
        repositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return repositorio.getResponseMsgError();
    }

    /**
     * Funcion encargada de mostrar la respuesta de actualizacion de una entrevista en la UI
     *
     * @return SingleLive evento de evento de mensajeria de actualizacion
     */
    public SingleLiveEvent<String> mostrarRespuestaActualizacion() {
        repositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return repositorio.getResponseMsgActualizacion();
    }

    public void refreshEntrevista(Entrevista entrevistaIntent) {
        repositorio = EntrevistaRepositorio.getInstancia(getApplication());
        repositorio.obtenerEntrevistaPersonal(entrevistaIntent);
    }
}
