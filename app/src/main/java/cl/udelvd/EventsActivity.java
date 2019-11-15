package cl.udelvd;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class EventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Eventos");

        setSupportActionBar(toolbar);
        //Boton atras
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        ViewPager viewPager = findViewById(R.id.view_pager_events);
        viewPager.setAdapter(new FragmentStatePageAdapter(getSupportFragmentManager(),BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));

        FloatingActionButton fb = findViewById(R.id.fb_crear_evento);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewEventDialog fragment = new NewEventDialog ();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, fragment)
                        .addToBackStack(null).commit();
            }
        });
    }
}
