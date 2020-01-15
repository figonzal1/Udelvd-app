package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.repositorios.EntrevistadoRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class EntrevistadoViewModel extends AndroidViewModel {

    private EntrevistadoRepositorio repositorio;

    private MutableLiveData<List<Entrevistado>> entrevistadoMutableLiveData;

    public EntrevistadoViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Funcion que envia el listado desde el Repositorio a la interfaz
     * (Con observer)
     *
     * @return MutableLiveData con listado de usuarios
     */
    public MutableLiveData<List<Entrevistado>> mostrarListaEntrevistados() {

        if (entrevistadoMutableLiveData == null) {
            entrevistadoMutableLiveData = new MutableLiveData<>();
            repositorio = EntrevistadoRepositorio.getInstance(getApplication());
            entrevistadoMutableLiveData = repositorio.obtenerEntrevistados();
        }
        return entrevistadoMutableLiveData;
    }

    /**
     * Funcion que refresca el listado de entrevistados (Forzar refresh)
     * (Funcion directa)
     */
    public void refreshListaEntrevistados() {
        repositorio = EntrevistadoRepositorio.getInstance(getApplication());
        repositorio.obtenerEntrevistados();
    }

    /**
     * Funcion que envia el mensaje de error desde el respositorio a la interfaz
     * (Con observer)
     *
     * @return SingleLive con mensaje de error
     */
    public SingleLiveEvent<String> mostrarErrorRespuesta() {
        repositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return repositorio.getErrorMsg();
    }

    /**
     * Funcion que envia el mensaje de respuesta de registro desde el respositorio a la interfaz
     * (Con observer)
     *
     * @return SingleLive con mensaje de registro
     */
    public SingleLiveEvent<String> mostrarRespuestaRegistro() {
        repositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return repositorio.getResponseMsgRegistro();
    }

    /**
     * Funcion que envía el mensaje de respuesta de actualización desde el repositorio hacia la inteefaz
     *
     * @return Single live con el mensaje de actualización
     */
    public SingleLiveEvent<String> mostrarRespuestaActualizacion() {
        repositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return repositorio.getResponseMsgActualizacion();
    }

    /**
     * Funcion encargada de enviar los datos del entrevistado desde el repositorio a la interfaz
     *
     * @param entrevistado Objeto entrevistado
     * @return Mutable Live data con los datos de la persona
     */
    public SingleLiveEvent<Entrevistado> mostrarEntrevistado(Entrevistado entrevistado) {
        repositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return repositorio.obtenerEntrevistado(entrevistado);
    }

    /**
     * Funcion que obliga a refrescar los datos del entrevistado
     *
     * @param entrevistado Datos del entrevistado
     */
    public void refreshEntrevistado(Entrevistado entrevistado) {
        repositorio = EntrevistadoRepositorio.getInstance(getApplication());
        repositorio.obtenerEntrevistado(entrevistado);
    }
}
