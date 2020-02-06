package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.repositorios.EntrevistadoRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class EntrevistadoListaViewModel extends AndroidViewModel {

    private EntrevistadoRepositorio entrevistadoRepositorio;

    public EntrevistadoListaViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getIsLoading();
    }

    /*
    LISTADO
     */
    public SingleLiveEvent<List<Entrevistado>> mostrarPrimeraPagina(int page) {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.obtenerEntrevistados(page);
    }

    public void cargarSiguientePagina(int page) {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        entrevistadoRepositorio.obtenerEntrevistados(page);
    }

    public SingleLiveEvent<List<Entrevistado>> mostrarSiguientePagina() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.obtenerSiguientePagina();
    }

    public void refreshListaEntrevistados() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        entrevistadoRepositorio.obtenerEntrevistados(1);
    }

    public SingleLiveEvent<String> mostrarMsgErrorListado() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getResponseMsgErrorListado();
    }

    public MutableLiveData<Integer> mostrarNEntrevistados() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getNEntrevistados();
    }

    /*
    ELIMINAR
     */
    public SingleLiveEvent<String> mostrarMsgErrorEliminar() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getResponseMsgErrorEliminar();
    }

    public SingleLiveEvent<String> mostrarMsgEliminar() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getResponseMsgEliminar();
    }
}
