package cl.udelvd;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NuevaEntrevistaActivity extends AppCompatActivity {

    private int id_entrevistado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_entrevista);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Crear entrevista");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id_entrevistado = bundle.getInt("id_entrevistado");
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            Intent intent = getIntent();
            //Enviar id_entrevistado de vuelta
            intent.putExtra("id_entrevistado", id_entrevistado);
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
