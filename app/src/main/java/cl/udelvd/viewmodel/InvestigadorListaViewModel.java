package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Investigador;
import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class InvestigadorListaViewModel extends AndroidViewModel {

    private InvestigadorRepositorio repositorio;

    public InvestigadorListaViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        repositorio = InvestigadorRepositorio.getInstance(getApplication());
        return repositorio.getIsLoading();
    }

    public MutableLiveData<Boolean> isActivando() {
        repositorio = InvestigadorRepositorio.getInstance(getApplication());
        return repositorio.getActivando();
    }

    public SingleLiveEvent<List<Investigador>> mostrarPrimeraPagina(int page, Investigador investigador) {
        repositorio = InvestigadorRepositorio.getInstance(getApplication());
        return repositorio.obtenerInvestigadores(page, investigador);
    }

    public void cargarSiguientePagina(int page, Investigador investigador) {
        repositorio = InvestigadorRepositorio.getInstance(getApplication());
        repositorio.obtenerInvestigadores(page, investigador);
    }

    public SingleLiveEvent<List<Investigador>> mostrarSiguientePagina() {
        repositorio = InvestigadorRepositorio.getInstance(getApplication());
        return repositorio.obtenerSiguientePagina();
    }

    public SingleLiveEvent<String> mostrarMsgErrorListado() {
        repositorio = InvestigadorRepositorio.getInstance(getApplication());
        return repositorio.getResponseMsgErrorListado();
    }

    public SingleLiveEvent<String> mostrarMsgActivacion() {
        repositorio = InvestigadorRepositorio.getInstance(getApplication());
        return repositorio.getResponseMsgActivacion();
    }

    public SingleLiveEvent<String> mostrarMsgErrorActivacion() {
        repositorio = InvestigadorRepositorio.getInstance(getApplication());
        return repositorio.getResponseMsgErrorActivacion();
    }

    public MutableLiveData<Integer> mostrarNEntrevistados() {
        repositorio = InvestigadorRepositorio.getInstance(getApplication());
        return repositorio.getNInvestigadores();
    }

    public void refreshInvestigadores(Investigador admin) {
        repositorio = InvestigadorRepositorio.getInstance(getApplication());
        repositorio.obtenerInvestigadores(1, admin);
    }
}
