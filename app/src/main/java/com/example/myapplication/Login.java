package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login1);

        //declarando elementos
        TextView titulo, text_ou;
        EditText nome, password;
        Button btn_login1, button5;
        ImageView avatar, lock;

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
        //a pessoa só pode fazer login se tiver preenchido todos os campos:
        btn_login1.setOnClickListener(view -> {
            String user = nome.getText().toString().trim();
            String pass = password.getText().toString().trim();
            //isso acima é basico pra pegar os valores digitados nos campos.

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
            // caso tudo esteja certinho, procede pra tela inicial
            Intent intent = new Intent(Login.this, Tela.class);
            startActivity(intent);
        });
    //fim do evento do botão. ainda falta uma parte pra programar:
    //caso os dados que a pessoa colocou não estejam no bd,
    //ela não pode entrar.
    //lembrete!
    }
}

