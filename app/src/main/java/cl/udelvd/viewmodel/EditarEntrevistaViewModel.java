package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.TipoEntrevista;
import cl.udelvd.repositorios.EntrevistaRepositorio;
import cl.udelvd.repositorios.TipoEntrevistaRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class EditarEntrevistaViewModel extends AndroidViewModel {

    private EntrevistaRepositorio entrevistaRepositorio;
    private TipoEntrevistaRepositorio tipoEntrevistaRepositorio;

    private MutableLiveData<List<TipoEntrevista>> tipoEntrevistaMutableLiveData;

    public EditarEntrevistaViewModel(@NonNull Application application) {
        super(application);
    }

    /*
    TIPOS ENTREVISTAS
     */
    public MutableLiveData<List<TipoEntrevista>> cargarTiposEntrevistas() {
        if (tipoEntrevistaMutableLiveData == null) {
            tipoEntrevistaMutableLiveData = new MutableLiveData<>();
            tipoEntrevistaRepositorio = TipoEntrevistaRepositorio.getInstancia(getApplication());
            tipoEntrevistaMutableLiveData = tipoEntrevistaRepositorio.obtenerTiposEntrevista();
        }
        return tipoEntrevistaMutableLiveData;
    }

    public SingleLiveEvent<String> mostrarMsgErrorTipoEntrevistaListado() {
        tipoEntrevistaRepositorio = TipoEntrevistaRepositorio.getInstancia(getApplication());
        return tipoEntrevistaRepositorio.getResponseMsgErrorListado();
    }

    public MutableLiveData<Boolean> isLoadingTiposEntrevistas() {
        tipoEntrevistaRepositorio = TipoEntrevistaRepositorio.getInstancia(getApplication());
        return tipoEntrevistaRepositorio.getIsLoading();
    }

    public void refreshTipoEntrevistas() {
        tipoEntrevistaRepositorio = TipoEntrevistaRepositorio.getInstancia(getApplication());
        tipoEntrevistaRepositorio.obtenerTiposEntrevista();
    }

    /*
    ENTREVISTA
     */
    public SingleLiveEvent<Entrevista> cargarEntrevista(Entrevista entrevista) {
        entrevistaRepositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return entrevistaRepositorio.obtenerEntrevistaPersonal(entrevista);
    }

    public MutableLiveData<Boolean> isLoadingEntrevista() {
        entrevistaRepositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return entrevistaRepositorio.getIsLoading();
    }

    public SingleLiveEvent<String> mostrarMsgErrorEntrevista() {
        entrevistaRepositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return entrevistaRepositorio.getResponseMsgErrorEntrevista();
    }

    public SingleLiveEvent<String> mostrarMsgActualizacion() {
        entrevistaRepositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return entrevistaRepositorio.getResponseMsgActualizacion();
    }

    public SingleLiveEvent<String> mostrarMsgErrorActualizacion() {
        entrevistaRepositorio = EntrevistaRepositorio.getInstancia(getApplication());
        return entrevistaRepositorio.getResponseMsgErrorActualizacion();
    }

    public void refreshEntrevista(Entrevista entrevistaIntent) {
        entrevistaRepositorio = EntrevistaRepositorio.getInstancia(getApplication());
        entrevistaRepositorio.obtenerEntrevistaPersonal(entrevistaIntent);
    }
}
