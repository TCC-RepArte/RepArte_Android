package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.api.ApiService;

public class SignUp extends AppCompatActivity {
    private ApiService apiService;
    private EditText nome, mail, password1, password2;
    private Button btn_login1;
    private TextView click01, texto1, titulo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        apiService = new ApiService(this);

        //iniciando os elementos pelo id
        titulo2 = findViewById(R.id.titulo2);
        click01 = findViewById(R.id.click01);
        texto1 = findViewById(R.id.texto1);
        nome = findViewById(R.id.username);
        mail = findViewById(R.id.email);
        password1 = findViewById(R.id.senha1);
        password2 = findViewById(R.id.senha2);
        btn_login1 = findViewById(R.id.btn_login1);

        //evento texto clickavel (voltar pra tela de login)
        //'clique aqui'
        click01.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
        });

        //evento do botão entrar
        btn_login1.setOnClickListener(v -> {
            String username = nome.getText().toString().trim();
            String email = mail.getText().toString().trim();
            String senha1 = password1.getText().toString();
            String senha2 = password2.getText().toString();

            //possiveis erros na hora de criar conta
            boolean hasError = false;

            // caso o campo de nome de usuário esteja vazio
            if (username.isEmpty()) {
                nome.setError("Campo obrigatório.");
                hasError = true;
            }

            // Verifica se tem espaço no nome de usuário
            if (username.contains(" ")) {
                nome.setError("O nome de usuário não pode conter espaços.");
                hasError = true;
            }

            // caso o campo de email esteja vazio
            if (email.isEmpty()) {
                mail.setError("Campo obrigatório.");
                hasError = true;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mail.setError("Digite um email válido.");
                hasError = true;
            }
            // caso o campo de senha1 esteja vazio
            if (senha1.isEmpty()) {
                password1.setError("Campo obrigatório.");
                hasError = true;
            }
            // caso o campo de senha2 esteja vazio
            if (senha2.isEmpty()) {
                password2.setError("Campo obrigatório.");
                hasError = true;
            }
            //verifica se as senhas são iguais
            if (!senha1.equals(senha2)) {
                password2.setError("As senhas não são iguais.");
                hasError = true;
            }
            //garante que a página nao vai seguir caso tenha um erro
            if (hasError) {
                return;
            }

            // Remove espaços do nome de usuário
            username = username.replace(" ", "");

            // Se passou por todas as validações, tenta cadastrar
            System.out.println("Tentando cadastrar usuário: " + username);
            System.out.println("Email informado: " + email);
            
            apiService.cadastrarUsuario(username, senha1, (e, result) -> {
                if (e != null) {
                    System.out.println("Erro de conexão: " + e.getMessage());
                    String mensagemErro = "Erro ao cadastrar: ";
                    if (e.getMessage().contains("Failed to connect")) {
                        mensagemErro += "Não foi possível conectar ao servidor";
                    } else {
                        mensagemErro += e.getMessage();
                    }
                    Toast.makeText(SignUp.this, mensagemErro, Toast.LENGTH_LONG).show();
                    return;
                }

                System.out.println("Resposta do servidor: " + (result != null ? result.toString() : "null"));

                if (result != null && result.has("status") && result.get("status").getAsString().equals("ok")) {
                    Toast.makeText(SignUp.this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show();
                    // Redireciona para tela de login
                    Intent intent = new Intent(SignUp.this, Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    String erro = result != null ? result.toString() : "resposta nula do servidor";
                    System.out.println("Erro no cadastro: " + erro);
                    Toast.makeText(SignUp.this, "Erro ao cadastrar usuário: " + erro, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}