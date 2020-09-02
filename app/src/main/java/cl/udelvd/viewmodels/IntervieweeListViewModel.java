package cl.udelvd.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import cl.udelvd.models.Interviewee;
import cl.udelvd.models.Researcher;
import cl.udelvd.repositories.IntervieweeRepository;
import cl.udelvd.utils.SingleLiveEvent;

public class IntervieweeListViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Interviewee>> filterList = new MutableLiveData<>();
    private IntervieweeRepository intervieweeRepository;

    public IntervieweeListViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> isLoading() {

        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getIsLoading();
    }

    /*
    LIST
     */
    public SingleLiveEvent<List<Interviewee>> loadFirstPage(int page, Researcher researcher, boolean totalList) {

        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getInterviewees(page, researcher, totalList);
    }

    public void loadNextPage(int page, Researcher researcher, boolean totalList) {

        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        intervieweeRepository.getInterviewees(page, researcher, totalList);
    }

    public SingleLiveEvent<List<Interviewee>> showNextPage() {

        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getNextPage();
    }

    public void refreshIntervieweeList(Researcher researcher, boolean listadoTotal) {

        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        intervieweeRepository.getInterviewees(1, researcher, listadoTotal);
    }

    public SingleLiveEvent<String> showMsgErrorList() {

        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getResponseMsgErrorList();
    }

    public MutableLiveData<Integer> showNInterviewees() {

        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getNInterviewees();
    }

    public MutableLiveData<List<Interviewee>> showFilteredIntervieweeList() {
        return filterList;
    }

    public void doSearch(List<Interviewee> intervieweeList, String s) {

        if (intervieweeList.size() > 0 && !s.isEmpty()) {

            //Lista utilizada para el searchView
            List<Interviewee> filteredList = new ArrayList<>();

            for (Interviewee l : intervieweeList) {

                //Filtrar por nombre completo
                if ((l.getName().toLowerCase() + " " + l.getLastName().toLowerCase()).contains(s)) {
                    filteredList.add(l);
                }

                //Filtrar por nombre
                else if (l.getName().toLowerCase().contains(s)) {
                    filteredList.add(l);
                }

                //Filtrar por apellido
                else if (l.getLastName().toLowerCase().contains(s)) {
                    filteredList.add(l);
                }
            }

            filterList.postValue(filteredList);

        } else {
            filterList.postValue(intervieweeList);
        }
    }

    /*
    DELETE
     */
    public SingleLiveEvent<String> showMsgErrorDelete() {

        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getResponseMsgErrorDelete();
    }

    public SingleLiveEvent<String> showMsgDelete() {

        intervieweeRepository = IntervieweeRepository.getInstance(getApplication());
        return intervieweeRepository.getResponseMsgDelete();
    }
}
