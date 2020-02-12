package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cl.udelvd.modelo.Ciudad;
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

public class NuevoEntrevistadoViewModel extends AndroidViewModel {


    private CiudadRepositorio ciudadRepositorio;
    private EstadoCivilRepositorio estadoCivilRepositorio;
    private NivelEducacionalRepositorio nivelEducacionalRepositorio;
    private TipoConvivenciaRepositorio tipoConvivenciaRepositorio;
    private ProfesionRepositorio profesionRepositorio;

    private EntrevistadoRepositorio entrevistadoRepositorio;

    private MutableLiveData<List<Ciudad>> ciudadMutableList;
    private MutableLiveData<List<EstadoCivil>> estadoCivilMutableList;
    private MutableLiveData<List<NivelEducacional>> nivelEducacionalMutableList;
    private MutableLiveData<List<TipoConvivencia>> tipoConvivenciaMutableList;
    private MutableLiveData<List<Profesion>> profesionMutableList;

    public NuevoEntrevistadoViewModel(@NonNull Application application) {
        super(application);
    }

    /*
    ENTREVISTADO
     */
    public SingleLiveEvent<String> mostrarMsgRegistro() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getResponseMsgRegistro();
    }

    public SingleLiveEvent<String> mostrarMsgErrorRegistro() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getResponseMsgErrorRegistro();
    }

    public MutableLiveData<Boolean> isLoadingEntrevistado() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getIsLoading();
    }

    /*
    CIUDADEs
     */
    public MutableLiveData<List<Ciudad>> cargarCiudades() {
        if (ciudadMutableList == null) {
            ciudadMutableList = new MutableLiveData<>();
            ciudadRepositorio = CiudadRepositorio.getInstancia(getApplication());
            ciudadMutableList = ciudadRepositorio.obtenerCiudades();
        }
        return ciudadMutableList;
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
        if (estadoCivilMutableList == null) {
            estadoCivilMutableList = new MutableLiveData<>();
            estadoCivilRepositorio = EstadoCivilRepositorio.getInstance(getApplication());
            estadoCivilMutableList = estadoCivilRepositorio.obtenerEstadosCiviles();
        }
        return estadoCivilMutableList;
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

        if (nivelEducacionalMutableList == null) {
            nivelEducacionalMutableList = new MutableLiveData<>();
            nivelEducacionalRepositorio = NivelEducacionalRepositorio.getInstancia(getApplication());
            nivelEducacionalMutableList = nivelEducacionalRepositorio.obtenerNivelesEducacionales();

        }
        return nivelEducacionalMutableList;
    }

    public MutableLiveData<Boolean> isLoadingNivelesEducacionales() {
        nivelEducacionalRepositorio = NivelEducacionalRepositorio.getInstancia(getApplication());
        return nivelEducacionalRepositorio.getIsLoading();
    }

    public SingleLiveEvent<String> mostrarMsgErrorListadoNivelesEduc() {
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

        if (tipoConvivenciaMutableList == null) {
            tipoConvivenciaMutableList = new MutableLiveData<>();
            tipoConvivenciaRepositorio = TipoConvivenciaRepositorio.getInstancia(getApplication());
            tipoConvivenciaMutableList = tipoConvivenciaRepositorio.obtenerTiposConvivencias();
        }
        return tipoConvivenciaMutableList;
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

        if (profesionMutableList == null) {
            profesionMutableList = new MutableLiveData<>();
            profesionRepositorio = ProfesionRepositorio.getInstancia(getApplication());
            profesionMutableList = profesionRepositorio.obtenerNivelesEducacionales();
        }
        return profesionMutableList;
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
