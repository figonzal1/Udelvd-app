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

    public MutableLiveData<List<Investigador>> cargarInvestigadores() {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
            repositorio = InvestigadorRepositorio.getInstance(getApplication());
            mutableLiveData = repositorio.obtenerInvestigadores();
        }

        return mutableLiveData;
    }

    public SingleLiveEvent<String> mostrarMsgErrorListado() {
        repositorio = InvestigadorRepositorio.getInstance(getApplication());
        return repositorio.getResponseMsgErrorListado();
    }
}
