package cl.udelvd.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import cl.udelvd.R;
import cl.udelvd.models.Event;
import cl.udelvd.utils.Utils;
import cl.udelvd.views.activities.EditEventActivity;
import cl.udelvd.views.fragments.dialog.DeleteDialogListener;
import cl.udelvd.views.fragments.dialog.DeleteEventDialogFragment;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class EventItemFragment extends Fragment {

    private static final int REQUEST_CODE_EDIT_EVENT = 300;

    private static final String TAG_DELETE_DIALOG_NAME = "EliminarEvento";
    private Event event;
    private String interviewDate;
    private int position;

    private TextView tvInterviewDate;
    private TextView tvEvent;
    private TextView tvAction;
    private ImageView ivEmoticon;
    private TextView tvEventHour;
    private TextView tvJustification;
    private TextView tvEmoticonDescription;
    private FloatingActionButton fbEditarEvento;
    private FloatingActionButton fbEliminarEvento;

    private Activity activity;
    private FragmentManager fragmentManager;
    private DeleteDialogListener listener;


    public EventItemFragment() {
    }

    public static EventItemFragment newInstance() {
        return new EventItemFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_swipe_event, container, false);

        instantiateInterfaceResources(v);

        setEventInformation();

        return v;
    }


    private void instantiateInterfaceResources(View v) {

        tvInterviewDate = v.findViewById(R.id.tv_interview_date);
        tvEvent = v.findViewById(R.id.tv_event_detail);
        tvEventHour = v.findViewById(R.id.tv_event_hour);
        tvAction = v.findViewById(R.id.tv_event_action);
        tvJustification = v.findViewById(R.id.tv_event_justification);
        ivEmoticon = v.findViewById(R.id.iv_icon_emoticon_event);
        tvEmoticonDescription = v.findViewById(R.id.tv_description_emoticon);

        fbEditarEvento = v.findViewById(R.id.fb_edit_event);
        fbEliminarEvento = v.findViewById(R.id.fb_delete_event);

    }

    private void setEventInformation() {

        tvInterviewDate.setText(String.format(getString(R.string.FORMATO_ENTREVISTA_FECHA), interviewDate));
        tvEvent.setText(String.format(Locale.US, getString(R.string.FORMATO_EVENTO_N), position + 1));

        tvAction.setText(String.valueOf(event.getAction().getName()));

        tvEventHour.setText(Utils.dateToString(getContext(), true, event.getEventHour()));

        tvJustification.setText(event.getJustification());

        tvEmoticonDescription.setText(event.getEmoticon().getDescription());

        //GLIDE PARA CARGAR IMAGEN DE foto
        Glide.with(this)
                .load(event.getEmoticon().getUrl())
                .apply(new RequestOptions()
                        .error(R.drawable.not_found))
                .transition(withCrossFade())
                .into(ivEmoticon);


        fbEditarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity.getApplicationContext(), EditEventActivity.class);
                intent.putExtra(getString(R.string.KEY_EVENTO_ID_ENTREVISTA), event.getInterview().getId());
                intent.putExtra(getString(R.string.KEY_EVENTO_ID_LARGO), event.getId());
                activity.startActivityForResult(intent, REQUEST_CODE_EDIT_EVENT);
            }
        });

        fbEliminarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeleteEventDialogFragment dialog = new DeleteEventDialogFragment(listener, event);
                dialog.show(fragmentManager, TAG_DELETE_DIALOG_NAME);
            }
        });
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setInterviewDate(String fecha_entrevista) {
        this.interviewDate = fecha_entrevista;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setListener(DeleteDialogListener listener) {
        this.listener = listener;
    }
}
