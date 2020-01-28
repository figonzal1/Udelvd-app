package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Ciudad;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.modelo.EstadoCivil;
import cl.udelvd.modelo.NivelEducacional;
import cl.udelvd.modelo.Profesion;
import cl.udelvd.modelo.TipoConvivencia;
import cl.udelvd.repositorios.CiudadRepositorio;
import cl.udelvd.repositorios.EntrevistadoRepositorio;
import cl.udelvd.repositorios.EstadoCivilRepositorio;
import cl.udelvd.repositorios.NivelEducacionalRepositorio;
import cl.udelvd.repositorios.ProfesionRepositorio;
import cl.udelvd.repositorios.TipoConvivenciaRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class EditarEntrevistadoViewModel extends AndroidViewModel {

    private CiudadRepositorio ciudadRepositorio;
    private EstadoCivilRepositorio estadoCivilRepositorio;
    private NivelEducacionalRepositorio nivelEducacionalRepositorio;
    private TipoConvivenciaRepositorio tipoConvivenciaRepositorio;
    private ProfesionRepositorio profesionRepositorio;

    private EntrevistadoRepositorio entrevistadoRepositorio;


    public EditarEntrevistadoViewModel(@NonNull Application application) {
        super(application);
    }


    /*
    ENTREVISTADO
     */
    public SingleLiveEvent<Entrevistado> cargarEntrevistado(Entrevistado entrevistado) {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.obtenerEntrevistado(entrevistado);
    }

    public SingleLiveEvent<String> mostrarMsgErrorEntrevistado() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getResponseMsgErrorEntrevistado();
    }

    public SingleLiveEvent<String> mostrarMsgActualizacion() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getResponseMsgActualizacion();
    }

    public SingleLiveEvent<String> mostrarMsgErrorActualizacion() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getResponseMsgErrorActualizacion();
    }

    public MutableLiveData<Boolean> isLoadingEntrevistado() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getIsLoading();
    }

    public void refreshEntrevistado(Entrevistado entrevistadoIntent) {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        entrevistadoRepositorio.obtenerEntrevistado(entrevistadoIntent);
    }

    /*
    CIUDADEs
     */
    public MutableLiveData<List<Ciudad>> cargarCiudades() {
        ciudadRepositorio = CiudadRepositorio.getInstancia(getApplication());
        return ciudadRepositorio.obtenerCiudades();
    }

    public SingleLiveEvent<String> mostrarMsgErrorListadoCiudades() {
        ciudadRepositorio = CiudadRepositorio.getInstancia(getApplication());
        return ciudadRepositorio.getResponseMsgErrorListado();
    }

    public MutableLiveData<Boolean> isLoadingCiudades() {
        ciudadRepositorio = CiudadRepositorio.getInstancia(getApplication());
        return ciudadRepositorio.getIsLoading();
    }

    public void refreshCiudades() {
        ciudadRepositorio = CiudadRepositorio.getInstancia(getApplication());
        ciudadRepositorio.obtenerCiudades();
    }

    /*
    ESTADO CIVIL
     */
    public MutableLiveData<List<EstadoCivil>> cargarEstadosCiviles() {
        estadoCivilRepositorio = EstadoCivilRepositorio.getInstance(getApplication());
        return estadoCivilRepositorio.obtenerEstadosCiviles();
    }

    public MutableLiveData<Boolean> isLoadingEstadosCiviles() {
        estadoCivilRepositorio = EstadoCivilRepositorio.getInstance(getApplication());
        return estadoCivilRepositorio.getIsLoading();
    }

    public SingleLiveEvent<String> mostrarMsgErrorListadoEstadosCiviles() {
        estadoCivilRepositorio = EstadoCivilRepositorio.getInstance(getApplication());
        return estadoCivilRepositorio.getResponseMsgErrorListado();
    }

    public void refreshEstadosCiviles() {
        estadoCivilRepositorio = EstadoCivilRepositorio.getInstance(getApplication());
        estadoCivilRepositorio.obtenerEstadosCiviles();
    }

    /*
    NIVEL EDUCACIONAL
     */

    public MutableLiveData<List<NivelEducacional>> cargarNivelesEducacionales() {
        nivelEducacionalRepositorio = NivelEducacionalRepositorio.getInstancia(getApplication());
        return nivelEducacionalRepositorio.obtenerNivelesEducacionales();
    }

    public MutableLiveData<Boolean> isLoadingNivelesEducacionales() {
        nivelEducacionalRepositorio = NivelEducacionalRepositorio.getInstancia(getApplication());
        return nivelEducacionalRepositorio.getIsLoading();
    }

    public SingleLiveEvent<String> mostrarMsgErrorNivelesEduc() {
        nivelEducacionalRepositorio = NivelEducacionalRepositorio.getInstancia(getApplication());
        return nivelEducacionalRepositorio.getResponseMsgErrorListado();
    }

    public void refreshNivelesEduc() {
        nivelEducacionalRepositorio = NivelEducacionalRepositorio.getInstancia(getApplication());
        nivelEducacionalRepositorio.obtenerNivelesEducacionales();
    }

    /*
    TIPO CONVIVENCIA
     */

    public MutableLiveData<List<TipoConvivencia>> cargarTiposConvivencia() {
        tipoConvivenciaRepositorio = TipoConvivenciaRepositorio.getInstancia(getApplication());
        return tipoConvivenciaRepositorio.obtenerTiposConvivencias();
    }

    public MutableLiveData<Boolean> isLoadingTiposConvivencias() {
        tipoConvivenciaRepositorio = TipoConvivenciaRepositorio.getInstancia(getApplication());
        return tipoConvivenciaRepositorio.getIsLoading();
    }

    public SingleLiveEvent<String> mostrarMsgErrorListadoTiposConvivencias() {
        tipoConvivenciaRepositorio = TipoConvivenciaRepositorio.getInstancia(getApplication());
        return tipoConvivenciaRepositorio.getResponseMsgErrorListado();
    }

    public void refreshTipoConvivencia() {
        tipoConvivenciaRepositorio = TipoConvivenciaRepositorio.getInstancia(getApplication());
        tipoConvivenciaRepositorio.obtenerTiposConvivencias();
    }

    /*
    PROFESION
     */

    public MutableLiveData<List<Profesion>> cargarProfesiones() {
        profesionRepositorio = ProfesionRepositorio.getInstancia(getApplication());
        return profesionRepositorio.obtenerNivelesEducacionales();
    }

    public MutableLiveData<Boolean> isLoadingProfesiones() {
        profesionRepositorio = ProfesionRepositorio.getInstancia(getApplication());
        return profesionRepositorio.getIsLoading();
    }

    public SingleLiveEvent<String> mostrarMsgErrorListadoProfesiones() {
        profesionRepositorio = ProfesionRepositorio.getInstancia(getApplication());
        return profesionRepositorio.getResponseMsgErrorListado();
    }

    public void refreshProfesiones() {
        profesionRepositorio = ProfesionRepositorio.getInstancia(getApplication());
        profesionRepositorio.obtenerNivelesEducacionales();
    }
}
