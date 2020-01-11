package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Emoticon;
import cl.udelvd.repositorios.EmoticonRepositorio;

public class EmoticonViewModel extends AndroidViewModel {

    private EmoticonRepositorio repositorio;
    private MutableLiveData<List<Emoticon>> emoticonMutableLiveData;

    public EmoticonViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Funcion encargada de obtener emoticones desde repositorio y enviarlos a interfaz por medio de ViewModel
     *
     * @return MutableLiveData con datos de emoticones
     */
    public MutableLiveData<List<Emoticon>> cargarEmoticones() {
        if (emoticonMutableLiveData == null) {
            emoticonMutableLiveData = new MutableLiveData<>();
            repositorio = EmoticonRepositorio.getInstancia(getApplication());
            emoticonMutableLiveData = repositorio.obtenerEmoticones();
        }
        return emoticonMutableLiveData;
    }
}
