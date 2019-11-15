package cl.udelvd;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InterviewsListActivity extends AppCompatActivity {

    private ConstraintLayout cv, cv2, cv3;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interviews_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Listado entrevistas");
        setSupportActionBar(toolbar);

        //Boton atras
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        cv = findViewById(R.id.entrevista1);
        TextView tv_titulo_1 = cv.findViewById(R.id.tv_entrevista_nombre);
        TextView tv_fecha_1 = cv.findViewById(R.id.tv_fecha_registro);

        tv_titulo_1.setText("Entrevista 1");
        tv_fecha_1.setText("06/12/2019");


        cv2 = findViewById(R.id.entrevista2);
        TextView tv_titulo_2 = cv2.findViewById(R.id.tv_entrevista_nombre);
        TextView tv_fecha_2 = cv2.findViewById(R.id.tv_fecha_registro);

        tv_titulo_2.setText("Entrevista 2");
        tv_fecha_2.setText("23/11/2019");


        cv3 = findViewById(R.id.entrevista3);
        TextView tv_titulo_3 = cv3.findViewById(R.id.tv_entrevista_nombre);
        TextView tv_fecha_3 = cv3.findViewById(R.id.tv_fecha_registro);
        TextView tv_extraordinaria_3 = cv3.findViewById(R.id.tv_tipo_entrevista);

        tv_titulo_3.setText("Entrevista 3");
        tv_fecha_3.setText("01/10/2019");
        tv_extraordinaria_3.setText("Extraordinaria");



        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InterviewsListActivity.this, EventsActivity.class);
                startActivity(intent);
            }
        });

        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InterviewsListActivity.this, EventsActivity.class);
                startActivity(intent);
            }
        });

        cv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InterviewsListActivity.this, EventsActivity.class);
                startActivity(intent);
            }
        });


        fab = findViewById(R.id.fb_crear_entrevista);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*NewUserDialog dialog = new NewUserDialog();
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "NewUserDialog");*/

                /*DialogFragment dialogFragment = new NewUserDialog();
                assert getFragmentManager() != null;
                dialogFragment.show(getFragmentManager(),"NewUserDialog");*/

                NewInterviewDialog fragment = new NewInterviewDialog ();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, fragment)
                        .addToBackStack(null).commit();
            }
        });
    }
}
