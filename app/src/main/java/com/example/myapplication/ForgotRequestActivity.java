package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.api.ApiService;

public class ForgotRequestActivity extends AppCompatActivity {
    private EditText etAlvo;
    private Button btnNext;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_request);

        apiService = new ApiService(this);

        etAlvo = findViewById(R.id.et_alvo);
        // pré-preenche se veio do login
        String prefill = getIntent().getStringExtra("alvo");
        if (prefill != null && !prefill.isEmpty()) etAlvo.setText(prefill);
        btnNext = findViewById(R.id.btn_next);

        btnNext.setOnClickListener(v -> {
            String alvo = etAlvo.getText().toString().trim();
            if (alvo.isEmpty()) {
                etAlvo.setError("Informe seu e-mail ou usuário");
                return;
            }

            btnNext.setEnabled(false);
            Toast.makeText(this, "Enviando código...", Toast.LENGTH_SHORT).show();
            apiService.enviarCodigoRecuperacao(alvo, (e, result) -> runOnUiThread(() -> {
                btnNext.setEnabled(true);
                if (e != null) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                Intent i = new Intent(this, ForgotOtpActivity.class);
                i.putExtra("alvo", alvo);
                startActivity(i);
            }));
        });
    }
}

