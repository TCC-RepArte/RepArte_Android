package com.example.myapplication.api;

import android.content.Context;
import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.async.future.FutureCallback;
import java.io.File;

public class ApiService {
    private static final String TAG = "ApiService";
    private static final String BASE_URL = "http://10.0.2.2/reparte/web/back-end/php/";
    private Context context;

    public ApiService(Context context) {
        this.context = context;
    }

    public void cadastrarUsuario(String usuario, String email, String senha, FutureCallback<String> callback) {
        // Remove espaços do nome de usuário e email
        usuario = usuario.replace(" ", "");
        email = email.replace(" ", "");
        
        // Gera um ID único para o usuário
        String id = generateUniqueId();
        
        String url = BASE_URL + "signup.php";
        Log.d(TAG, "=== INÍCIO DO CADASTRO ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Dados enviados - Usuário: " + usuario);
        Log.d(TAG, "Dados enviados - Email: " + email);
        Log.d(TAG, "Dados enviados - ID: " + id);
        
        Ion.with(context)
                .load("POST", url)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Accept", "*/*")
                .setBodyParameter("usuario", usuario)
                .setBodyParameter("email", email)
                .setBodyParameter("senha", senha)
                .setBodyParameter("confsenha", senha)
                .setBodyParameter("id", id)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e == null && result != null && result.contains("success")) {
                            // Se deu sucesso, salva o ID para usar na próxima etapa
                            // Você pode salvar em SharedPreferences ou passar via Intent
                            saveUserId(id);
                        }
                        callback.onCompleted(e, result);
                    }
                });
    }

    // Método para salvar o ID do usuário para usar na próxima etapa
    private void saveUserId(String id) {
        context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
               .edit()
               .putString("user_id", id)
               .apply();
    }

    // Método para a segunda etapa do cadastro
    public void completarCadastro(String nomeExibicao, String descricao, File foto, FutureCallback<String> callback) {
        // Recupera o ID salvo da primeira etapa
        String id = context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                          .getString("user_id", null);

        if (id == null) {
            callback.onCompleted(new Exception("ID do usuário não encontrado"), null);
            return;
        }

        String url = BASE_URL + "signup2.php";
        Log.d(TAG, "=== COMPLETANDO CADASTRO ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Dados enviados - Nome: " + nomeExibicao);
        Log.d(TAG, "Dados enviados - ID: " + id);
        
        Ion.with(context)
                .load("POST", url)
                .setHeader("Accept", "*/*")
                .setMultipartParameter("nomeexi", nomeExibicao)
                .setMultipartParameter("desc", descricao)
                .setMultipartParameter("id", id)
                .setMultipartFile("envft", foto)
                .asString()
                .setCallback(callback);
    }

    // id unico
    private String generateUniqueId() {
        return "U" + System.currentTimeMillis() + "-" + Math.random() * 1000;
    }

    public void realizarLogin(String usuario, String senha, FutureCallback<String> callback) {
        // Remove espaços do nome de usuário
        usuario = usuario.replace(" ", "");
        
        String url = BASE_URL + "login.php";
        Log.d(TAG, "=== INÍCIO DO LOGIN ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Dados enviados - Usuário: " + usuario);
        Log.d(TAG, "Dados enviados - Senha: " + senha);

        Ion.with(context)
                .load("POST", url)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Accept", "*/*")
                .setBodyParameter("usuario", usuario)
                .setBodyParameter("senha", senha)
                .asString()
                .setCallback(callback);
    }
}