package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Usuario;
import cl.udelvd.repositorios.UsuarioRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class UsuarioViewModel extends AndroidViewModel {

    private UsuarioRepositorio usuarioRepositorio;

    public UsuarioViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Funcion que envia el listado desde el Repositorio a la interfaz
     * (Con observer)
     *
     * @return MutableLiveData con listado de usuarios
     */
    public MutableLiveData<List<Usuario>> mostrarListaUsuarios() {
        usuarioRepositorio = UsuarioRepositorio.getInstance(getApplication());
        return usuarioRepositorio.getUsuarios();
    }

    /**
     * Funcion que refresca el listado de ususuarios
     * (Funcion directa)
     */
    public void refreshListaUsuarios() {
        usuarioRepositorio = UsuarioRepositorio.getInstance(getApplication());
        usuarioRepositorio.getUsuarios();
    }

    /**
     * Funcion que envia el mensaje de error desde el respositorio a la interfaz
     * (Con observer)
     *
     * @return SingleLive con mensaje de error
     */
    public SingleLiveEvent<String> mostrarErrorRespuesta() {
        usuarioRepositorio = UsuarioRepositorio.getInstance(getApplication());
        return usuarioRepositorio.getErrorMsg();
    }

}
