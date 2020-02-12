package cl.udelvd.vistas.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.modelo.Investigador;
import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utilidades.SnackbarInterface;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity implements SnackbarInterface {

    private static final int REGISTRAR_INVESTIGADOR_CODE = 200;
    private TextInputLayout ilEmail;
    private TextInputLayout ilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private ProgressBar progressBar;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        instanciarRecursosInterfaz();

        iniciarViewModels();

        configurarlinkRegistro();

        configurarlinkRecuperar();

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            Log.d(getString(R.string.TAG_DYNAMIC_LINK_FIREBASE), String.valueOf(deepLink));


                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

                            boolean isReset = sharedPreferences.getBoolean(getString(R.string.SHARED_PREF_RESET_PASS), false);

                            if (!isReset) {

                                Intent intent = new Intent(LoginActivity.this, ResetearPassActivity.class);
                                startActivity(intent);

                                finish();
                            } else {
                                showSnackbar(findViewById(R.id.login_investigador), Snackbar.LENGTH_INDEFINITE, getString(R.string.DYNAMIC_LINK_INVALIDO), getString(R.string.SNACKBAR_SOLICITAR_RECUPERACION));
                            }
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(getString(R.string.TAG_DYNAMIC_LINK_FIREBASE), "getDynamicLink:onFailure", e);
                    }
                });
    }

    /**
     * Funcion encargada de configurar las views de la vista Login
     */
    private void instanciarRecursosInterfaz() {
        //Instancia formulario
        //Inputs Layouts
        ilEmail = findViewById(R.id.il_email_login);
        ilPassword = findViewById(R.id.il_password_login);

        //Edit texts
        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_password_login);

        //Barra de progreso horizontal
        progressBar = findViewById(R.id.progress_horizontal_login);

        //Boton de login
        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validarCampos()) {
                    progressBar.setVisibility(View.VISIBLE);

                    //Obtener datos desde fomulario
                    Investigador investigador = new Investigador();
                    investigador.setEmail(Objects.requireNonNull(etEmail.getText()).toString().toLowerCase());

                    //TODO: Deberia enviarse un hash desde cliente para seguridad de password
                    investigador.setPassword(Objects.requireNonNull(etPassword.getText()).toString());

                    InvestigadorRepositorio.getInstance(getApplication()).loginInvestigador(investigador);
                }
            }
        });

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    /**
     * Funcion encargada de manejar los ViewModelObservers de la actividad login
     */
    private void iniciarViewModels() {

        //Observador de carga
        loginViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    ilEmail.setEnabled(false);
                    etEmail.setEnabled(false);

                    ilPassword.setEnabled(false);
                    etPassword.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);

                    ilEmail.setEnabled(true);
                    etEmail.setEnabled(true);

                    ilPassword.setEnabled(true);
                    etPassword.setEnabled(true);
                }
            }
        });

        //Observador de Mensajeria Para Login Correcto
        loginViewModel.mostrarMsgLogin().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> stringObjectMap) {

                Investigador investigador = (Investigador) stringObjectMap.get(getString(R.string.KEY_INVES_OBJECT));

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY),
                        Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (investigador != null) {

                    Log.d(getString(R.string.TAG_VM_INVES_LOGIN), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), investigador.toString()));

                    //Guardar en sharedPref investigador recien logeado
                    editor.putInt(getString(R.string.SHARED_PREF_INVES_ID), investigador.getId());
                    editor.putString(getString(R.string.SHARED_PREF_INVES_NOMBRE), investigador.getNombre());
                    editor.putString(getString(R.string.SHARED_PREF_INVES_APELLIDO), investigador.getApellido());
                    editor.putString(getString(R.string.SHARED_PREF_INVES_EMAIL), investigador.getEmail());
                    editor.putInt(getString(R.string.SHARED_PREF_INVES_ID_ROL), investigador.getIdRol());
                    editor.putString(getString(R.string.SHARED_PREF_INVES_NOMBRE_ROL), investigador.getNombreRol());
                    editor.putBoolean(getString(R.string.SHARED_PREF_INVES_ACTIVADO), investigador.isActivado());
                    editor.putString(getString(R.string.SHARED_PREF_INVES_CREATE_TIME), investigador.getCreateTime());

                    //TODO: Almacenar hash de password y no texto plano
                    editor.putString(getString(R.string.SHARED_PREF_INVES_PASSWORD), Objects.requireNonNull(etPassword.getText()).toString());
                    editor.apply();

                    String msg_login = (String) stringObjectMap.get(getString(R.string.LOGIN_MSG_VM));

                    progressBar.setVisibility(View.INVISIBLE);


                    //Si el mensaje es 'Bienvenido' se realiza login
                    assert msg_login != null;
                    if (msg_login.equals(getString(R.string.MSG_INVEST_LOGIN))) {

                        //Mostrar mensaje en pantalla
                        //Toast.makeText(getApplicationContext(), msg_login, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra(getString(R.string.INTENT_KEY_MSG_LOGIN), msg_login);
                        startActivity(intent);
                        finish();
                    }

                }
            }

        });

        //Observador de Mensajeria Para Login Incorrecto
        loginViewModel.mostrarMsgErrorLogin().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);

                Log.d(getString(R.string.TAG_VM_INVES_LOGIN), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

                showSnackbar(findViewById(R.id.login_investigador), Snackbar.LENGTH_LONG, s, null);
            }
        });
    }

    /**
     * Validacion de campos para formulario Login
     *
     * @return True|False dependiendo de los errores
     */
    private boolean validarCampos() {

        int contador_errores = 0;

        //Comprobar email vacio
        if (TextUtils.isEmpty(etEmail.getText())) {
            ilEmail.setErrorEnabled(true);
            ilEmail.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {

            //Comprobar mail válido
            if (Utils.isInValidEmail(etEmail.getText())) {
                ilEmail.setErrorEnabled(true);
                ilEmail.setError(getString(R.string.VALIDACION_EMAIL));
                contador_errores++;
            } else {
                ilEmail.setErrorEnabled(false);
            }
        }

        //Comprobar contraseña vacia
        if (TextUtils.isEmpty(etPassword.getText())) {
            ilPassword.setErrorEnabled(true);
            ilPassword.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } //Comprobar contraseña menor que 8
        else if (etPassword.getText().length() < 8) {
            ilPassword.setErrorEnabled(true);
            ilPassword.setError(getString(R.string.VALIDACION_PASSWORD_LARGO));
            contador_errores++;
        } else {
            ilPassword.setErrorEnabled(false);
        }

        //Si no hay errores, pasa a registro
        return contador_errores == 0;
    }

    /**
     * Funcion encargada de manejar la logica del link azul "Registro"
     */
    private void configurarlinkRegistro() {
        //Logica de textview de registro
        TextView tv_registro = findViewById(R.id.tv_registro);
        tv_registro.setMovementMethod(LinkMovementMethod.getInstance());
        Spannable spans = (Spannable) tv_registro.getText();
        ClickableSpan clickSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {

                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivityForResult(intent, REGISTRAR_INVESTIGADOR_CODE);
            }
        };


        if (Locale.getDefault().getLanguage().equals(getString(R.string.LANGUAJE_EN))) {
            String registro = tv_registro.getText().toString();
            int index = registro.indexOf("Sign up");
            spans.setSpan(clickSpan, index, spans.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (Locale.getDefault().getLanguage().equals(getString(R.string.LANGUAJE_ES))) {
            String registro = tv_registro.getText().toString();
            int index = registro.indexOf("Regístrate");
            spans.setSpan(clickSpan, index, spans.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * Funcion encargada de manejar la logica del link azul "Olvidar cuenta"
     */
    private void configurarlinkRecuperar() {
        //Logica de textview de registro
        TextView tv_recuperacion = findViewById(R.id.tv_recuperar);
        tv_recuperacion.setMovementMethod(LinkMovementMethod.getInstance());
        Spannable spans = (Spannable) tv_recuperacion.getText();
        ClickableSpan clickSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {

                Intent intent = new Intent(LoginActivity.this, RecuperacionActivity.class);
                startActivity(intent);
            }
        };
        spans.setSpan(clickSpan, 0, spans.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REGISTRAR_INVESTIGADOR_CODE) {

            if (resultCode == RESULT_OK) {

                assert data != null;
                Bundle bundle = data.getExtras();

                assert bundle != null;
                showSnackbar(findViewById(R.id.login_investigador), Snackbar.LENGTH_LONG, bundle.getString(getString(R.string.INTENT_KEY_MSG_REGISTRO)), null);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showSnackbar(View v, int largo_snackbar, String titulo, String accion) {

        Snackbar snackbar = Snackbar.make(v, titulo, largo_snackbar);

        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RecuperacionActivity.class);
                    startActivity(intent);
                }
            });
        }
        snackbar.show();

    }
}
