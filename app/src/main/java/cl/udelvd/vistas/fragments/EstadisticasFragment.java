package cl.udelvd.vistas.fragments;

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
import cl.udelvd.vistas.activities.WebViewActivity;


public class EstadisticasFragment extends Fragment {

    //private final String URL_GRAFICO_DISTRIBUCIONES = "https://rb.gy/eknxh6";
    private final String URL_GRAFICO_DISTRIBUCIONES = "https://rebrand.ly/oe2fton";

    public EstadisticasFragment() {
        // Required empty public constructor
    }

    public static EstadisticasFragment newInstance() {
        return new EstadisticasFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        configurarBoton(v);

        configurarLinkDirecto(v);

        return v;
    }

    private void configurarLinkDirecto(View v) {
        //Logica de textview de registro
        TextView tv_registro = v.findViewById(R.id.tv_link_corto);

        int index = URL_GRAFICO_DISTRIBUCIONES.indexOf("//");
        String subtring = URL_GRAFICO_DISTRIBUCIONES.substring(index + 2);
        tv_registro.setText(String.format(getString(R.string.FORMATO_LINK_DIRECTO), subtring));

        tv_registro.setMovementMethod(LinkMovementMethod.getInstance());

        Spannable spans = (Spannable) tv_registro.getText();
        ClickableSpan clickSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {

                //Configurar portapapeles
                ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(getString(R.string.INTENT_LINK_GRAFICO), URL_GRAFICO_DISTRIBUCIONES);

                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getContext(), R.string.COPIADO_PORTAPAPELES, Toast.LENGTH_LONG).show();
                }

            }
        };
        spans.setSpan(clickSpan, tv_registro.getText().toString().indexOf("rebrand.ly"), spans.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void configurarBoton(View v) {
        Button button = v.findViewById(R.id.btn_go);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(getString(R.string.INTENT_LINK_GRAFICO), URL_GRAFICO_DISTRIBUCIONES);
                startActivity(intent);
            }
        });
    }

}
