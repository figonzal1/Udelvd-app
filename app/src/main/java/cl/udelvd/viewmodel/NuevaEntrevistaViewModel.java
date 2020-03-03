package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.TipoEntrevista;
import cl.udelvd.repositorios.EntrevistaRepositorio;
import cl.udelvd.repositorios.TipoEntrevistaRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class NuevaEntrevistaViewModel extends AndroidViewModel {

    private TipoEntrevistaRepositorio tipoEntrevistaRepositorio;
    private EntrevistaRepositorio entrevistaRepositorio;

    private MutableLiveData<List<TipoEntrevista>> tipoEntrevistaMutable;

    public NuevaEntrevistaViewModel(@NonNull Application application) {
        super(application);
    }

    /*
        TIPOS DE ENTREVISTAS
     */
    public MutableLiveData<List<TipoEntrevista>> cargarTiposEntrevistas() {
        if (tipoEntrevistaMutable == null) {
            tipoEntrevistaMutable = new MutableLiveData<>();
            tipoEntrevistaRepositorio = TipoEntrevistaRepositorio.getInstancia(getApplication());
            tipoEntrevistaMutable = tipoEntrevistaRepositorio.obtenerTiposEntrevista();
        }
        return tipoEntrevistaMutable;
    }

    public SingleLiveEvent<String> mostrarMsgErrorTiposEntrevistas() {
        tipoEntrevistaRepositorio = TipoEntrevistaRepositorio.getInstancia(getApplication());
        return tipoEntrevistaRepositorio.getResponseMsgErrorListado();
    }

    public MutableLiveData<Boolean> isLoadingTiposEntrevistas() {
        tipoEntrevistaRepositorio = TipoEntrevistaRepositorio.getInstancia(getApplication());
        return tipoEntrevistaRepositorio.getIsLoading();
    }


    /*
        ENTREVISTADOS
     */
    public SingleLiveEvent<String> mostrarMsgErrorRegistro() {
        entrevistaRepositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return entrevistaRepositorio.getResponseMsgErrorRegistro();
    }

    public SingleLiveEvent<String> mostrarMsgRegistro() {
        entrevistaRepositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return entrevistaRepositorio.getResponseMsgRegistro();
    }

    /**
     * Carga mensajeria desde repositorio a UI (Registro)
     *
     * @return MutableLiveData
     */
    public MutableLiveData<Boolean> isLoadingRegistroEntrevista() {
        entrevistaRepositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return entrevistaRepositorio.getIsLoading();
    }

    public void refreshTipoEntrevistas() {
        tipoEntrevistaRepositorio = TipoEntrevistaRepositorio.getInstancia(getApplication());
        tipoEntrevistaRepositorio.obtenerTiposEntrevista();
    }
}
