package com.example.myapplication;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.api.ApiService;

public class ForgotResetActivity extends AppCompatActivity {
    private EditText etP1, etP2;
    private ImageView btnToggle1, btnToggle2;
    private Button btnSubmit;
    private ApiService apiService;
    private String alvo, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_reset);

        apiService = new ApiService(this);
        alvo = getIntent().getStringExtra("alvo");
        token = getIntent().getStringExtra("token");

        etP1 = findViewById(R.id.et_password1);
        etP2 = findViewById(R.id.et_password2);
        btnToggle1 = findViewById(R.id.btn_toggle_p1);
        btnToggle2 = findViewById(R.id.btn_toggle_p2);
        btnSubmit = findViewById(R.id.btn_submit);

        btnToggle1.setOnClickListener(v -> toggle(etP1, btnToggle1));
        btnToggle2.setOnClickListener(v -> toggle(etP2, btnToggle2));

        btnSubmit.setOnClickListener(v -> {
            String s1 = etP1.getText().toString();
            String s2 = etP2.getText().toString();
            if (s1.isEmpty()) { etP1.setError("Campo obrigatório"); return; }
            if (s2.isEmpty()) { etP2.setError("Campo obrigatório"); return; }
            if (!s1.equals(s2)) { etP2.setError("As senhas não coincidem"); return; }

            btnSubmit.setEnabled(false);
            Toast.makeText(this, "Salvando nova senha...", Toast.LENGTH_SHORT).show();
            apiService.redefinirSenha(alvo, token, s1, (e, result) -> runOnUiThread(() -> {
                btnSubmit.setEnabled(true);
                if (e != null) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show();
                finish();
            }));
        });
    }

    private void toggle(EditText et, ImageView btn) {
        boolean hidden = (et.getInputType() & InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType.TYPE_TEXT_VARIATION_PASSWORD;
        if (hidden) {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btn.setImageResource(R.drawable.ic_visibility);
        } else {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btn.setImageResource(R.drawable.ic_visibility_off);
        }
        et.setSelection(et.getText().length());
    }
}

