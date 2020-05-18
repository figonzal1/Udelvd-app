package cl.udelvd.views.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import cl.udelvd.R;
import cl.udelvd.views.activities.WebViewActivity;


public class StatsFragment extends Fragment {

    //private final String URL_GRAFICO_DISTRIBUCIONES = "https://rb.gy/eknxh6";
    private final String URL_EVENT_DISTRIBUTION = "https://rebrand.ly/oe2fton";

    public StatsFragment() {
    }

    public static StatsFragment newInstance() {
        return new StatsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, container, false);

        configButton(v);

        configDirectLink(v);

        return v;
    }

    private void configDirectLink(View v) {

        TextView tvRegistry = v.findViewById(R.id.tv_short_link);

        int index = URL_EVENT_DISTRIBUTION.indexOf("//");
        String subtring = URL_EVENT_DISTRIBUTION.substring(index + 2);
        tvRegistry.setText(String.format(getString(R.string.FORMATO_LINK_DIRECTO), subtring));

        tvRegistry.setMovementMethod(LinkMovementMethod.getInstance());

        Spannable spans = (Spannable) tvRegistry.getText();
        ClickableSpan clickSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {


                ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(getString(R.string.INTENT_LINK_GRAFICO), URL_EVENT_DISTRIBUTION);

                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getContext(), R.string.COPIADO_PORTAPAPELES, Toast.LENGTH_LONG).show();
                }

            }
        };
        spans.setSpan(clickSpan, tvRegistry.getText().toString().indexOf("rebrand.ly"), spans.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void configButton(View v) {
        Button button = v.findViewById(R.id.btn_go);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(getString(R.string.INTENT_LINK_GRAFICO), URL_EVENT_DISTRIBUTION);
                startActivity(intent);
            }
        });
    }

}
