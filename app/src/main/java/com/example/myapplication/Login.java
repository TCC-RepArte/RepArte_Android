package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.api.ApiService;

public class Login extends AppCompatActivity {
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

        //evento do botão de registrar (ir pra página de sign-up)
        button5.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        });

        // evento do botão 'entrar'
        btn_login1.setOnClickListener(view -> {
            String user = nome.getText().toString().trim();
            String pass = password.getText().toString().trim();

            // verifica se o campo do username foi preenchido e avisa se nao foi
            if (user.isEmpty()) {
                nome.setError("Preencha o nome de usuário.");
                nome.requestFocus();
                return;
            }
            // verifica se o campo da senha foi preenchido e avisa se nao foi
            if (pass.isEmpty()) {
                password.setError("Preencha a senha.");
                password.requestFocus();
                return;
            }

            // Tenta fazer login usando a API
            apiService.realizarLogin(user, pass, (e, result) -> {
                if (e != null) {
                    System.out.println("Erro de conexão: " + e.getMessage());
                    String mensagemErro = "Erro ao fazer login: ";
                    if (e.getMessage().contains("Failed to connect")) {
                        mensagemErro += "Não foi possível conectar ao servidor";
                    } else {
                        mensagemErro += e.getMessage();
                    }
                    Toast.makeText(Login.this, mensagemErro, Toast.LENGTH_LONG).show();
                    return;
                }

                System.out.println("Resposta do servidor: " + (result != null ? result.toString() : "null"));

                if (result != null && result.has("status") && result.get("status").getAsString().equals("ok")) {
                    Toast.makeText(Login.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, Tela.class);
                    startActivity(intent);
                    finish(); // fecha a activity de login
                } else {
                    String erro = result != null ? result.toString() : "resposta nula do servidor";
                    System.out.println("Erro no login: " + erro);
                    Toast.makeText(Login.this, "Usuário ou senha incorretos", Toast.LENGTH_LONG).show();
                    password.setText(""); // limpa o campo de senha
                }
            });
        });
    }
}