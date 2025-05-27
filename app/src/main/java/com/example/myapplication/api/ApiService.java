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
        String usuarioTemp = usuarioOriginal.replace(" ", "").toLowerCase(); // Converter para minúsculas
        // Adiciona @ se não começar com @
        if (!usuarioTemp.startsWith("@")) {
            usuarioTemp = "@" + usuarioTemp;
        }
        final String usuario = usuarioTemp;
        email = email.replace(" ", "");
        
        // Gerar ID com tamanho correto (12 caracteres)
        String id = generateUniqueId();
        if (id.length() > 12) {
            id = id.substring(0, 12);
        }
        
        final String finalId = id;
        String url = BASE_URL + "cadastro.php";
        Log.d(TAG, "=== INÍCIO DO CADASTRO ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Dados enviados - Usuário: " + usuario);
        Log.d(TAG, "Dados enviados - Email: " + email);
        Log.d(TAG, "Dados enviados - ID: " + finalId);
        Log.d(TAG, "Dados enviados - Senha (comprimento): " + senha.length());
        
        Ion.with(context)
                .load("POST", url)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Accept", "*/*")
                .setBodyParameter("usuario", usuario)
                .setBodyParameter("email", email)
                .setBodyParameter("senha", senha)
                .setBodyParameter("id", finalId)
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
                            // Salvar credenciais para uso posterior no login
                            saveUserId(finalId);
                            saveUserCredentials(usuario, senha);
                            Log.d(TAG, "Credenciais salvas - Usuário: " + usuario);
                            Log.d(TAG, "Credenciais salvas - ID: " + finalId);
                            callback.onCompleted(null, "success");
                        } else {
                            Log.e(TAG, "Erro no cadastro. Resposta: " + result);
                            callback.onCompleted(new Exception("Erro no cadastro: " + result), null);
                        }
                    }
                });
    }

    private void saveUserCredentials(String usuario, String senha) {
        Log.d(TAG, "Salvando credenciais nas SharedPreferences");
        Log.d(TAG, "Usuário sendo salvo: " + usuario);
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
        // Gera um ID alfanumérico de 12 caracteres
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf(Math.random()).substring(2, 6);
        String id = timestamp.substring(timestamp.length() - 6) + random;
        return id.substring(0, 12);
    }

    public void realizarLogin(String usuarioOriginal, String senha, FutureCallback<String> callback) {
        // Remove espaços e converte para minúsculas
        String usuarioTemp = usuarioOriginal.replace(" ", "").toLowerCase();
        
        Log.d(TAG, "=== DETALHES DO LOGIN ===");
        Log.d(TAG, "Usuário original: " + usuarioOriginal);
        Log.d(TAG, "Usuário após limpar espaços: " + usuarioTemp);
        
        // Se o usuário parece ser um email, extrair apenas o nome antes do @
        if (usuarioTemp.contains("@")) {
            String antigousuario = usuarioTemp;
            usuarioTemp = usuarioTemp.split("@")[0];
            Log.d(TAG, "Usuário continha @, convertendo de '" + antigousuario + "' para '" + usuarioTemp + "'");
        }
        
        // Adiciona @ se não começar com @
        if (!usuarioTemp.startsWith("@")) {
            String antigousuario = usuarioTemp;
            usuarioTemp = "@" + usuarioTemp;
            Log.d(TAG, "Adicionando @ ao usuário, convertendo de '" + antigousuario + "' para '" + usuarioTemp + "'");
        }
        
        final String usuario = usuarioTemp;
        Log.d(TAG, "Usuário final que será enviado: '" + usuario + "'");
        
        // Usar o mesmo caminho que funcionou no cadastro
        String url = BASE_URL + "login.php";
        Log.d(TAG, "=== INÍCIO DO LOGIN ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Dados enviados - Usuário: " + usuario);
        Log.d(TAG, "Dados enviados - Senha (tamanho): " + senha.length());
        
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

                        if (result != null && (result.contains("success") || result.contains("\"status\":\"success\""))) {
                            Log.d(TAG, "Login realizado com sucesso!");
                            callback.onCompleted(null, "success");
                        } else {
                            Log.e(TAG, "Erro no login. Resposta detalhada: " + result);
                            // Se falhou com @, tentar sem @
                            if (usuario.startsWith("@")) {
                                String usuarioSemArroba = usuario.substring(1);
                                Log.d(TAG, "Tentando login novamente sem @: " + usuarioSemArroba);
                                realizarLogin(usuarioSemArroba, senha, callback);
                                return;
                            }
                            callback.onCompleted(null, result);
                        }
                    }
                });
    }
}