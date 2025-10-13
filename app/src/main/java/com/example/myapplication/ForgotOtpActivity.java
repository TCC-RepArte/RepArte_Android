package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.api.ApiService;

public class ForgotOtpActivity extends AppCompatActivity {
    private EditText etCodigo;
    private Button btnValidar;
    private android.widget.TextView tvResend;
    private ApiService apiService;
    private String alvo;
    private android.os.CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_otp);

        apiService = new ApiService(this);
        alvo = getIntent().getStringExtra("alvo");

        etCodigo = findViewById(R.id.et_codigo);
        btnValidar = findViewById(R.id.btn_validar);
        tvResend = findViewById(R.id.tv_resend);

        // força 6 dígitos numéricos
        etCodigo.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(6)});
        etCodigo.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        btnValidar.setOnClickListener(v -> {
            String codigo = etCodigo.getText().toString().trim();
            if (codigo.length() < 4) {
                etCodigo.setError("Código inválido");
                return;
            }

            btnValidar.setEnabled(false);
            Toast.makeText(this, "Validando código...", Toast.LENGTH_SHORT).show();
            apiService.verificarCodigoRecuperacao(alvo, codigo, (e, result) -> runOnUiThread(() -> {
                btnValidar.setEnabled(true);
                if (e != null) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                Intent i = new Intent(this, ForgotResetActivity.class);
                i.putExtra("alvo", alvo);
                i.putExtra("token", result); // pode ser um token de reset retornado pelo backend
                startActivity(i);
            }));
        });

        // Cooldown de 60s para reenviar
        tvResend.setOnClickListener(v -> {
            tvResend.setEnabled(false);
            tvResend.setText("Reenviando...");
            apiService.enviarCodigoRecuperacao(alvo, (e, result) -> runOnUiThread(() -> {
                if (e != null) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    tvResend.setEnabled(true);
                    tvResend.setText("Reenviar código");
                    return;
                }
                Toast.makeText(this, "Código reenviado", Toast.LENGTH_SHORT).show();
                iniciarCooldown();
            }));
        });
    }

    private void iniciarCooldown() {
        if (timer != null) timer.cancel();
        timer = new android.os.CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long s = millisUntilFinished / 1000;
                tvResend.setText("Reenviar código em " + s + "s");
                tvResend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                tvResend.setText("Reenviar código");
                tvResend.setEnabled(true);
            }
        };
        timer.start();
    }
}

