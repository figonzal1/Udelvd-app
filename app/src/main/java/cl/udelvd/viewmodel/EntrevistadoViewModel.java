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
    public MutableLiveData<List<Entrevistado>> mostrarListaUsuarios() {

        if (entrevistadoMutableLiveData == null) {
            entrevistadoMutableLiveData = new MutableLiveData<>();
            repositorio = EntrevistadoRepositorio.getInstance(getApplication());
            entrevistadoMutableLiveData = repositorio.obtenerEntrevistados();
        }
        return entrevistadoMutableLiveData;
    }

    /**
     * Funcion que refresca el listado de ususuarios (Forzar getUsuario)
     * (Funcion directa)
     */
    public void refreshListaUsuarios() {
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

    public MutableLiveData<Entrevistado> mostrarEntrevistado(Entrevistado entrevistado) {
        repositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return repositorio.obtenerEntrevistado(entrevistado);
    }
}
