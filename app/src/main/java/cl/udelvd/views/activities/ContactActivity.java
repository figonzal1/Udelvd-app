package cl.udelvd.views.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import cl.udelvd.R;
import cl.udelvd.utils.Utils;

public class ContactActivity extends AppCompatActivity {

    private TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Utils.configToolbar(this, getApplicationContext(), R.drawable.ic_arrow_back_black_24dp, getString(R.string.TITULO_TOOLBAR_CONTACTO));

        tvEmail = findViewById(R.id.tv_contact_email_value);
        tvEmail.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableStringBuilder spans = new SpannableStringBuilder(tvEmail.getText());
        ClickableSpan clickSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {

                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(getString(R.string.INTENT_LINK_GRAFICO), tvEmail.getText());

                if (clipboardManager != null) {

                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getApplicationContext(), R.string.COPIADO_PORTAPAPELES, Toast.LENGTH_LONG).show();
                }

            }
        };

        spans.setSpan(clickSpan, 0, spans.length(), 0);
        tvEmail.setText(spans);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}