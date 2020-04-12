package cl.udelvd.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.modelo.Investigador;
import cl.udelvd.repositorios.EntrevistadoRepositorio;
import cl.udelvd.utilidades.SingleLiveEvent;

public class EntrevistadoListaViewModel extends AndroidViewModel {

    private EntrevistadoRepositorio entrevistadoRepositorio;

    private final MutableLiveData<List<Entrevistado>> filterList = new MutableLiveData<>();

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
    public SingleLiveEvent<List<Entrevistado>> mostrarPrimeraPagina(int page, Investigador investigador, boolean listadoTotal) {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.obtenerEntrevistados(page, investigador, listadoTotal);
    }

    public void cargarSiguientePagina(int page, Investigador investigador, boolean listadoTotal) {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        entrevistadoRepositorio.obtenerEntrevistados(page, investigador, listadoTotal);
    }

    public SingleLiveEvent<List<Entrevistado>> mostrarSiguientePagina() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.obtenerSiguientePagina();
    }

    public void refreshListaEntrevistados(Investigador investigador, boolean listadoTotal) {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        entrevistadoRepositorio.obtenerEntrevistados(1, investigador, listadoTotal);
    }

    public SingleLiveEvent<String> mostrarMsgErrorListado() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getResponseMsgErrorListado();
    }

    public MutableLiveData<Integer> mostrarNEntrevistados() {
        entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
        return entrevistadoRepositorio.getNEntrevistados();
    }

    public MutableLiveData<List<Entrevistado>> showFilteredQuakeList() {
        return filterList;
    }

    public void doSearch(List<Entrevistado> entrevistadoList, String s) {

        if (entrevistadoList.size() > 0 && !s.isEmpty()) {
            //Lista utilizada para el searchView
            List<Entrevistado> filteredList = new ArrayList<>();

            for (Entrevistado l : entrevistadoList) {

                //Filtrar por nombre completo
                if ((l.getNombre().toLowerCase() + " " + l.getApellido().toLowerCase()).contains(s)) {
                    filteredList.add(l);
                }

                //Filtrar por nombre
                else if (l.getNombre().toLowerCase().contains(s)) {
                    filteredList.add(l);
                }

                //Filtrar por apellido
                else if (l.getApellido().toLowerCase().contains(s)) {
                    filteredList.add(l);
                }
            }
            filterList.postValue(filteredList);
        } else {
            filterList.postValue(entrevistadoList);
        }

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
