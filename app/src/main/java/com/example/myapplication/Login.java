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

        // Anima os elementos da tela de login
        animateLoginElements();

        //evento do botão de registrar (ir pra página de sign-up)
        button5.setOnClickListener(view -> {
            // Aplica animação de clique
            AppAnimationUtils.animateButtonClick(button5, () -> {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        });

        // evento do botão 'entrar'
        btn_login1.setOnClickListener(view -> {
            // Aplica animação de clique
            AppAnimationUtils.animateButtonClick(btn_login1, () -> {
                String user = nome.getText().toString().trim();
                String pass = password.getText().toString().trim();

                Log.d(TAG, "Tentando fazer login com usuário: " + user);

                // verifica se o campo do username foi preenchido e avisa se nao foi
                if (user.isEmpty()) {
                    nome.setError("Preencha o nome de usuário.");
                    AppAnimationUtils.animateShake(nome);
                    nome.requestFocus();
                    return;
                }

                // Verifica se o usuário começa com @
                if (!user.startsWith("@")) {
                    nome.setError("O nome de usuário deve começar com @");
                    AppAnimationUtils.animateShake(nome);
                    nome.requestFocus();
                    return;
                }

                // verifica se o campo da senha foi preenchido e avisa se nao foi
                if (pass.isEmpty()) {
                    password.setError("Preencha a senha.");
                    AppAnimationUtils.animateShake(password);
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

                            // Anima o sucesso com bounce
                            AppAnimationUtils.animateBounce(btn_login1);

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
                            // Anima o erro com shake
                            AppAnimationUtils.animateShake(btn_login1);
                            password.setText(""); // Limpa a senha
                        }
                    });
                });
            });
        });
    }

    /**
     * Anima os elementos da tela de login
     */
    private void animateLoginElements() {
        // Anima o título com fade in
        if (titulo != null) {
            titulo.setAlpha(0f);
            titulo.setTranslationY(-50f);
            titulo.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(800)
                    .setStartDelay(200)
                    .start();
        }

        // Anima os ícones com fade in
        if (avatar != null) {
            avatar.setAlpha(0f);
            avatar.setScaleX(0.8f);
            avatar.setScaleY(0.8f);
            avatar.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setStartDelay(400)
                    .start();
        }

        if (lock != null) {
            lock.setAlpha(0f);
            lock.setScaleX(0.8f);
            lock.setScaleY(0.8f);
            lock.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setStartDelay(500)
                    .start();
        }

        // Anima os campos de entrada com slide up
        if (nome != null) {
            nome.setAlpha(0f);
            nome.setTranslationY(30f);
            nome.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(600)
                    .start();
        }

        if (password != null) {
            password.setAlpha(0f);
            password.setTranslationY(30f);
            password.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(700)
                    .start();
        }

        // Anima os botões com slide up
        if (btn_login1 != null) {
            btn_login1.setAlpha(0f);
            btn_login1.setTranslationY(30f);
            btn_login1.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(800)
                    .start();
        }

        if (button5 != null) {
            button5.setAlpha(0f);
            button5.setTranslationY(30f);
            button5.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(900)
                    .start();
        }

        // Anima o texto "ou" com fade in
        if (text_ou != null) {
            text_ou.setAlpha(0f);
            text_ou.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setStartDelay(1000)
                    .start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Limpa os campos quando a tela é retomada
        nome.setText("");
        password.setText("");
    }
}