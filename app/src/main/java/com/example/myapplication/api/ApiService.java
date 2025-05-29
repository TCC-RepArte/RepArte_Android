package com.example.myapplication.api;

import android.content.Context;
import android.util.Log;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.async.future.FutureCallback;
import java.io.File;

public class ApiService {
    private static final String TAG = "ApiService";
    private static final String BASE_URL = "http://192.168.0.110/reparte/web/back-end/php/";
    private Context context;

    public ApiService(Context context) {
        this.context = context;
    }

    public void cadastrarUsuario(String usuarioOriginal, String email, String senha, FutureCallback<String> callback) {
        String usuarioTemp = usuarioOriginal.replace(" ", "").toLowerCase();
        if (usuarioTemp.startsWith("@")) {
            usuarioTemp = usuarioTemp.substring(1);
        }
        final String usuario = usuarioTemp;
        email = email.replace(" ", "");

        String id = generateUniqueId();
        if (id.length() > 12) {
            id = id.substring(0, 12);
        }

        final String finalId = id;
        String url = BASE_URL + "signup.php";
        Log.d(TAG, "=== INÍCIO DO CADASTRO ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Dados enviados - Usuário: " + usuario);
        Log.d(TAG, "Dados enviados - Email: " + email);
        Log.d(TAG, "Dados enviados - ID: " + finalId);
        Log.d(TAG, "Dados enviados - Senha (comprimento): " + senha.length());

        // Configuração do Ion com mais opções de rede
        Ion.getDefault(context)
           .configure()
           .setLogging("IonLog", Log.DEBUG)
           .connectTimeout(20000)
           .followRedirects(true)
           .maxRedirects(5);

        Ion.with(context)
                .load("POST", url)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Connection", "close")
                .setTimeout(20000)
                .setBodyParameter("usuario", usuario)
                .setBodyParameter("email", email)
                .setBodyParameter("senha", senha)
                .setBodyParameter("confsenha", senha)
                .setBodyParameter("id", finalId)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e(TAG, "Erro detalhado na requisição de cadastro: ", e);
                            Log.e(TAG, "Tipo de erro: " + e.getClass().getSimpleName());
                            Log.e(TAG, "Mensagem de erro: " + e.getMessage());
                            callback.onCompleted(e, null);
                            return;
                        }

                        Log.d(TAG, "Resposta do cadastro: " + result);
                        if (result != null && !result.toLowerCase().contains("erro")) {
                            Log.d(TAG, "Cadastro realizado com sucesso!");
                            saveUserId(finalId);
                            saveUserCredentials("@" + usuario, senha);
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
        String random = String.valueOf(Math.random());

        // Pega os últimos 6 dígitos do timestamp
        String timestampPart = timestamp.substring(Math.max(0, timestamp.length() - 6));

        // Pega 6 dígitos do número aleatório (após o ponto decimal)
        String randomPart = random.substring(2); // Remove "0."
        if (randomPart.length() > 6) {
            randomPart = randomPart.substring(0, 6);
        } else {
            // Se não tiver 6 dígitos, completa com zeros
            while (randomPart.length() < 6) {
                randomPart += "0";
            }
        }

        // Combina as duas partes para formar um ID de 12 caracteres
        return timestampPart + randomPart;
    }

    public void realizarLogin(String usuarioOriginal, String senha, FutureCallback<String> callback) {
        // Remove espaços e converte para minúsculas
        String usuarioTemp = usuarioOriginal.replace(" ", "").toLowerCase();

        Log.d(TAG, "=== DETALHES DO LOGIN ===");
        Log.d(TAG, "Usuário original: " + usuarioOriginal);
        Log.d(TAG, "Usuário após limpar espaços: " + usuarioTemp);

        if (usuarioTemp.contains("@")) {
            String antigousuario = usuarioTemp;
            usuarioTemp = usuarioTemp.split("@")[0];
            Log.d(TAG, "Usuário continha @, convertendo de '" + antigousuario + "' para '" + usuarioTemp + "'");
        }

        if (!usuarioTemp.startsWith("@")) {
            String antigousuario = usuarioTemp;
            usuarioTemp = "@" + usuarioTemp;
            Log.d(TAG, "Adicionando @ ao usuário, convertendo de '" + antigousuario + "' para '" + usuarioTemp + "'");
        }

        final String usuario = usuarioTemp;
        // 🛠️ URL corrigida:
        String url = BASE_URL + "login_android.php";
        Log.d(TAG, "=== INÍCIO DO LOGIN ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Dados enviados - Usuário: " + usuario);
        Log.d(TAG, "Dados enviados - Senha (tamanho): " + senha.length());

        Ion.with(context)
                .load("POST", url)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Accept", "*/*")
                .setTimeout(30000) // 30 segundos de timeout
                .setBodyParameter("usuario", usuario)
                .setBodyParameter("senha", senha)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e(TAG, "Erro detalhado na requisição de login: ", e);
                            Log.e(TAG, "Tipo de erro: " + e.getClass().getSimpleName());
                            Log.e(TAG, "Mensagem de erro: " + e.getMessage());
                            if (e.getCause() != null) {
                                Log.e(TAG, "Causa do erro: " + e.getCause().getMessage());
                            }
                            callback.onCompleted(e, null);
                            return;
                        }

                        Log.d(TAG, "Resposta do login: " + result);

                        if (result != null && (result.contains("success") || result.contains("\"status\":\"success\""))) {
                            Log.d(TAG, "Login realizado com sucesso!");
                            callback.onCompleted(null, "success");
                        } else {
                            Log.e(TAG, "Erro no login. Resposta detalhada: " + result);
                            if (usuario.startsWith("@")) {
                                String usuarioSemArroba = usuario.substring(1);
                                Log.d(TAG, "Tentando login novamente sem @: " + usuarioSemArroba);
                                realizarLogin(usuarioSemArroba, senha, callback);
                            } else {
                                callback.onCompleted(new Exception("Usuário ou senha incorretos"), null);
                            }
                        }
                    }
                });
    }
}