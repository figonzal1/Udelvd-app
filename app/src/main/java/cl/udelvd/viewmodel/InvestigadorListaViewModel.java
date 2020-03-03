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
    private MutableLiveData<List<Investigador>> mutableLiveData;

    public InvestigadorListaViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {
        repositorio = InvestigadorRepositorio.getInstance(getApplication());
        return repositorio.getIsLoading();
    }

    public MutableLiveData<List<Investigador>> cargarInvestigadores(Investigador investigador) {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
            repositorio = InvestigadorRepositorio.getInstance(getApplication());
            mutableLiveData = repositorio.obtenerInvestigadores(investigador);
        }

        return mutableLiveData;
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

    public void refreshInvestigadores(Investigador admin) {
        repositorio = InvestigadorRepositorio.getInstance(getApplication());
        repositorio.obtenerInvestigadores(admin);
    }
}
