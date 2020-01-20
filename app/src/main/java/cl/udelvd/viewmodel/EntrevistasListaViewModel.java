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

public class EntrevistasListaViewModel extends AndroidViewModel {

    private EntrevistaRepositorio entrevistaRepositorio;

    private MutableLiveData<List<Entrevista>> entrevistaMutableLiveData;

    public EntrevistasListaViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Funcion encargada de enviar listado de entrevistas desde repositorio a UI
     *
     * @param entrevistado Objeto entrevistado con datos para busqueda de lista
     * @return MutableLiveData con listado de entrevistas
     */
    public MutableLiveData<List<Entrevista>> cargarEntrevistas(Entrevistado entrevistado) {

        if (entrevistaMutableLiveData == null) {
            entrevistaMutableLiveData = new MutableLiveData<>();
            entrevistaRepositorio = EntrevistaRepositorio.getInstancia(getApplication());
            entrevistaMutableLiveData = entrevistaRepositorio.obtenerEntrevistasPersonales(entrevistado);
        }

        return entrevistaMutableLiveData;

    }

    /**
     * Funcion para forzar refresco del listado de entrevistado
     *
     * @param entrevistado Obteto con datos para busqueda
     */
    public void refreshEntrevistas(Entrevistado entrevistado) {
        entrevistaRepositorio = EntrevistaRepositorio.getInstancia(getApplication());
        entrevistaRepositorio.obtenerEntrevistasPersonales(entrevistado);
    }

    /**
     * Funcion encargada de mostrar los erroresde listado
     *
     * @return SingleLiveEvent de mensajeria
     */
    public SingleLiveEvent<String> mostrarErroresListado() {
        entrevistaRepositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return entrevistaRepositorio.getResponseMsgErrorListado();
    }

    /**
     * Funcion para el manejo de Progress dialog desde repositorio a UI
     *
     * @return MutableLiveData con booleano para carga
     */
    public MutableLiveData<Boolean> isLoading() {
        entrevistaRepositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return entrevistaRepositorio.getIsLoading();
    }
}
