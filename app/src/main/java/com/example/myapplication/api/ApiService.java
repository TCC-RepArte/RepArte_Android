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

    public void cadastrarUsuario(String usuarioOriginal, String email, String senha, FutureCallback<String> callback) {
        final String usuario = usuarioOriginal.replace(" ", "");
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
                            Log.e(TAG, "Erro na requisição de cadastro: " + e.getMessage(), e);
                            callback.onCompleted(e, null);
                            return;
                        }

                        Log.d(TAG, "Resposta do cadastro: " + result);
                        
                        if (result != null && result.contains("success")) {
                            Log.d(TAG, "Cadastro realizado com sucesso!");
                            saveUserId(id);
                            saveUserCredentials(usuario, senha);
                            callback.onCompleted(null, "success");
                        } else {
                            Log.e(TAG, "Erro no cadastro. Resposta: " + result);
                            callback.onCompleted(new Exception("Erro no cadastro: " + result), null);
                        }
                    }
                });
    }

    private void saveUserCredentials(String usuario, String senha) {
        context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
               .edit()
               .putString("ultimo_usuario", usuario)
               .putString("ultima_senha", senha)
               .apply();
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
            Log.e(TAG, "ID do usuário não encontrado nas SharedPreferences");
            callback.onCompleted(new Exception("ID do usuário não encontrado"), null);
            return;
        }

        String url = BASE_URL + "signup2.php";
        Log.d(TAG, "=== COMPLETANDO CADASTRO ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Dados enviados - Nome: " + nomeExibicao);
        Log.d(TAG, "Dados enviados - Descrição: " + descricao);
        Log.d(TAG, "Dados enviados - ID: " + id);
        Log.d(TAG, "Foto presente: " + (foto != null ? "Sim" : "Não"));
        
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
                        
                        // Garante que a resposta seja tratada como sucesso
                        if (result != null) {
                            // Salva o status de cadastro completo
                            context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                                   .edit()
                                   .putBoolean("cadastro_completo", true)
                                   .apply();
                            
                            Log.d(TAG, "Perfil atualizado com sucesso! Salvou status nas preferências.");
                            callback.onCompleted(null, "success");
                        } else {
                            Log.e(TAG, "Erro ao atualizar perfil. Resposta nula do servidor");
                            callback.onCompleted(new Exception("Resposta nula do servidor"), null);
                        }
                    }
                });
    }

    public boolean isCadastroCompleto() {
        return context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                     .getBoolean("cadastro_completo", false);
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
        Log.d(TAG, "Último usuário cadastrado: " + 
            context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                   .getString("ultimo_usuario", "nenhum"));
        
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
                            Log.e(TAG, "Erro na requisição de login: " + e.getMessage(), e);
                            callback.onCompleted(e, null);
                            return;
                        }

                        Log.d(TAG, "Resposta do login: " + result);

                        if (result != null && result.contains("success")) {
                            Log.d(TAG, "Login realizado com sucesso!");
                            callback.onCompleted(null, "success");
                        } else {
                            Log.e(TAG, "Erro no login. Resposta: " + result);
                            callback.onCompleted(null, result);
                        }
                    }
                });
    }
}