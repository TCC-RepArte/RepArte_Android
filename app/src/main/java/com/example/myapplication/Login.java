package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.api.ApiService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    private ApiService apiService;
    private EditText nome, password;
    private Button btn_login1, button5;
    private TextView titulo, text_ou;
    private ImageView avatar, lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login1);

        apiService = new ApiService(this);

        // iniciando elementos pela id
        titulo = findViewById(R.id.titulo);
        text_ou = findViewById(R.id.text_ou);
        nome = findViewById(R.id.username);
        password = findViewById(R.id.senha1);
        btn_login1 = findViewById(R.id.btn_login1);
        button5 = findViewById(R.id.button5);
        avatar = findViewById(R.id.imageView14);
        lock = findViewById(R.id.imageView15);

        // Limpa os campos ao iniciar
        nome.setText("");
        password.setText("");

        //evento do botão de registrar (ir pra página de sign-up)
        button5.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // evento do botão 'entrar'
        btn_login1.setOnClickListener(view -> {
            String user = nome.getText().toString().trim();
            String pass = password.getText().toString().trim();

            Log.d(TAG, "Tentando fazer login com usuário: " + user);

            // verifica se o campo do username foi preenchido e avisa se nao foi
            if (user.isEmpty()) {
                nome.setError("Preencha o nome de usuário.");
                nome.requestFocus();
                return;
            }

            // Verifica se o usuário começa com @
            if (!user.startsWith("@")) {
                nome.setError("O nome de usuário deve começar com @");
                nome.requestFocus();
                return;
            }

            // verifica se o campo da senha foi preenchido e avisa se nao foi
            if (pass.isEmpty()) {
                password.setError("Preencha a senha.");
                password.requestFocus();
                return;
            }

            // Mostra progresso
            Toast.makeText(Login.this, "Fazendo login...", Toast.LENGTH_SHORT).show();

            // Tenta fazer login usando a API
            apiService.realizarLogin(user, pass, (e, result) -> {
                runOnUiThread(() -> {
                    if (e != null) {
                        Log.e(TAG, "Erro no login", e);
                        Toast.makeText(Login.this, 
                            e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (result != null && result.contains("success")) {
                        Log.d(TAG, "Login bem-sucedido!");
                        Toast.makeText(Login.this, 
                            "Bem-vindo(a) de volta!", 
                            Toast.LENGTH_SHORT).show();

                        // Pequeno delay para mostrar a mensagem de boas-vindas
                        new android.os.Handler().postDelayed(() -> {
                            Intent intent = new Intent(Login.this, Tela.class);
                            startActivity(intent);
                            finish();
                        }, 1000);
                    } else {
                        Log.e(TAG, "Login falhou. Resposta: " + result);
                        Toast.makeText(Login.this, 
                            "Usuário ou senha incorretos", 
                            Toast.LENGTH_LONG).show();
                        password.setText(""); // Limpa a senha
                    }
                });
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Limpa os campos quando a tela é retomada
        nome.setText("");
        password.setText("");
    }
}