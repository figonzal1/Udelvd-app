package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.Map;

import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class InvestigadorViewModel extends AndroidViewModel {

    private InvestigadorRepositorio investigadorRepositorio;

    public InvestigadorViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Funcion encargada de mostrar mensaje de registro correcto, luego de realizar operacion de registro
     *
     * @return SingleLiveEvent Objeto viewModel que es llamado solo una vez
     */
    public SingleLiveEvent<String> mostrarMsgRespuestaRegistro() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsgRegistro();
    }

    /**
     * Funcion encargada de mostrar mensaje de login, luego de realizar operacion de login correct
     * @return SingleLiveEvent Objeto viewModel que es llamado solo una vez
     */
    public SingleLiveEvent<Map<String, Object>> mostrarMsgRespuestaLogin() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsgLogin();
    }

    /**
     * Funcion encargada de mostrar mensaje de error para cualquier operacion sobre investigador (Login, Registro, Actualizacion)
     *
     * @return SingleLiveEvent Objeto viewModel que es llamado solo una vez
     */
    public SingleLiveEvent<String> mostrarErrorRespuesta() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getErrorMsg();
    }

    /**
     * Funcion encargada de mostrar mensaje de actualizacion en interfaz luego de realizar una actualizacion de investigador
     *
     * @return SingleLiveEvent Objeto viewModel que es llamado solo una vez
     */
    public SingleLiveEvent<Map<String, Object>> mostrarMsgRespuestaActualizacion() {
        investigadorRepositorio = InvestigadorRepositorio.getInstance(getApplication());
        return investigadorRepositorio.getResponseMsgActualizacion();
    }
}
