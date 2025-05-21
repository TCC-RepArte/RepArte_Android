package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);


        //declarando os elementos
        TextView titulo2, click01, texto1;
        EditText nome, mail, password1, password2;
        Button btn_login1;

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

        //evento do botão entrar (por enquanto, direcionar pra página chamada Login)
        //so pode funcionar se os campos tiverem sido preenchidos adequadamente.
        btn_login1.setOnClickListener(v -> {
            String username = nome.getText().toString().trim();
            String email = mail.getText().toString().trim();
            String senha1 = password1.getText().toString();
            String senha2 = password2.getText().toString();
            //isso acima recebe oq foi escrito em cada campo

                         //possiveis erros na hora de criar conta
            boolean hasError = false;

            // caso o campo de nome de usuário esteja vazio
            if (username.isEmpty()) {
                nome.setError("Campo obrigatório.");
                hasError = true;
            }
            // caso o campo de email esteja vazio
            if (email.isEmpty()) {
                mail.setError("Campo obrigatório.");
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
            //garante que a página nao vai seguir caso tenha um erro desses acima
            if (hasError) {
                return;
            }

            // evento do botão caso tudo tenha dado certo:
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
        });//fim do botão


    }
}//fim do código

