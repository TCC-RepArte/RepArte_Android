package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.api.ApiService;
import com.koushikdutta.async.future.FutureCallback;

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
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

            // Desabilita o botão para evitar duplo clique
            btn_login1.setEnabled(false);

            // Mostra progresso para o usuário
            Toast.makeText(SignUp.this, "Criando sua conta...", Toast.LENGTH_SHORT).show();

            // Tenta cadastrar o usuário
            apiService.cadastrarUsuario(username, email, senha1, new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {
                    runOnUiThread(() -> {
                        btn_login1.setEnabled(true);
                        
                        if (e != null) {
                            Toast.makeText(SignUp.this, "Erro ao criar conta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (result != null && result.equals("success")) {
                            Toast.makeText(SignUp.this, "Complete seu perfil!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignUp.this, Perfil.class);
                            intent.putExtra("novo_cadastro", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignUp.this, "Erro ao criar conta. Tente novamente.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        });
    }
}