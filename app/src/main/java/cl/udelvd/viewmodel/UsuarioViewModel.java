package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.model.Usuario;
import cl.udelvd.repositorios.UsuarioRepositorio;
import cl.udelvd.utils.SingleLiveEvent;

public class UsuarioViewModel extends AndroidViewModel {

    private UsuarioRepositorio usuarioRepositorio;

    public UsuarioViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<Usuario>> mostrarListaUsuarios() {
        usuarioRepositorio = UsuarioRepositorio.getInstance(getApplication());
        return usuarioRepositorio.getUsuarios();
    }

    public SingleLiveEvent<Boolean> checkearTokenLogin() {
        usuarioRepositorio = UsuarioRepositorio.getInstance(getApplication());
        return usuarioRepositorio.isValidToken();
    }

}
