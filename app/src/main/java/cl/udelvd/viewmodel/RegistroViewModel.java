package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import cl.udelvd.repositorios.RegistroRepositorio;

public class RegistroViewModel extends AndroidViewModel {

    private RegistroRepositorio registroRepositorio;

    public RegistroViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> mostrarMsgRespuesta() {

        registroRepositorio = RegistroRepositorio.getInstance(getApplication());
        return registroRepositorio.getResponseMsg();

    }
}
