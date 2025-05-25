package com.example.myapplication.api;

import android.content.Context;
import android.util.Log;
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
        usuario = usuario.replace(" ", "");
        email = email.replace(" ", "");
        
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
                        if (e != null) {
                            Log.e(TAG, "Erro na requisição: " + e.getMessage(), e);
                            callback.onCompleted(e, null);
                            return;
                        }

                        Log.d(TAG, "Cadastro realizado com sucesso!");
                        saveUserId(id);
                        callback.onCompleted(null, "success");
                    }
                });
    }

    private void saveUserId(String id) {
        context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
               .edit()
               .putString("user_id", id)
               .apply();
    }

    public void completarCadastro(String nomeExibicao, String descricao, File foto, FutureCallback<String> callback) {
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
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e(TAG, "Erro na requisição: " + e.getMessage(), e);
                            callback.onCompleted(e, null);
                            return;
                        }

                        Log.d(TAG, "Resposta do servidor: " + (result != null ? result : "null"));
                        Log.d(TAG, "Tamanho da resposta: " + (result != null ? result.length() : 0));

                        if (result != null && result.contains("success")) {
                            Log.d(TAG, "Perfil atualizado com sucesso!");
                        } else {
                            Log.e(TAG, "Erro ao atualizar perfil. Resposta: " + result);
                        }
                        callback.onCompleted(e, result);
                    }
                });
    }

    private String generateUniqueId() {
        return "U" + System.currentTimeMillis() + "-" + Math.random() * 1000;
    }

    public void realizarLogin(String usuario, String senha, FutureCallback<String> callback) {
        usuario = usuario.replace(" ", "");
        
        String url = BASE_URL + "login.php";
        Log.d(TAG, "=== INÍCIO DO LOGIN ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Dados enviados - Usuário: " + usuario);
        
        Ion.with(context)
                .load("POST", url)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Accept", "*/*")
                .setBodyParameter("usuario", usuario)
                .setBodyParameter("senha", senha)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e(TAG, "Erro na requisição: " + e.getMessage(), e);
                            callback.onCompleted(e, null);
                            return;
                        }

                        Log.d(TAG, "Resposta do servidor: " + (result != null ? result : "null"));
                        Log.d(TAG, "Tamanho da resposta: " + (result != null ? result.length() : 0));

                        if (result != null && result.contains("success")) {
                            Log.d(TAG, "Login realizado com sucesso!");
                        } else {
                            Log.e(TAG, "Erro no login. Resposta: " + result);
                        }
                        callback.onCompleted(e, result);
                    }
                });
    }
}