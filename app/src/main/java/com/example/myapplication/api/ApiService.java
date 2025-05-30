package com.example.myapplication.api;

import android.content.Context;
import android.util.Log;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.async.future.FutureCallback;
import java.io.File;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ApiService {
    private static final String TAG = "ApiService";
    private static final String BASE_URL = "http://192.168.1.180/reparte/web/back-end/php/";
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

        // Configuração básica do Ion
        Ion.getDefault(context)
                .configure()
                .setLogging("IonLog", Log.DEBUG);

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
        Log.d(TAG, "Salvando ID do usuário nas SharedPreferences: " + id);
        context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                .edit()
                .putString("user_id", id)
                .apply();
        
        // Verifica se o ID foi salvo corretamente
        String savedId = context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                .getString("user_id", null);
        Log.d(TAG, "ID salvo com sucesso? " + (savedId != null && savedId.equals(id)));
    }

    public void completarCadastro(String nomeExibicao, String descricao, File foto, FutureCallback<String> callback) {
        Log.d(TAG, "Iniciando completarCadastro...");
        String id = context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                .getString("user_id", null);

        Log.d(TAG, "ID recuperado das SharedPreferences: " + id);

        if (id == null) {
            Log.e(TAG, "ID do usuário não encontrado nas SharedPreferences");
            callback.onCompleted(new Exception("ID do usuário não encontrado"), null);
            return;
        }

        String url = BASE_URL + "signup02.php";
        Log.d(TAG, "=== COMPLETANDO CADASTRO ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Dados enviados - Nome: " + nomeExibicao);
        Log.d(TAG, "Dados enviados - Descrição: " + descricao);
        Log.d(TAG, "Dados enviados - ID: " + id);
        Log.d(TAG, "Foto presente: " + (foto != null ? "Sim" : "Não"));
        if (foto != null) {
            Log.d(TAG, "Detalhes da foto - Tamanho: " + foto.length() + " bytes");
            Log.d(TAG, "Detalhes da foto - Caminho: " + foto.getAbsolutePath());
        }

        Ion.with(context)
                .load("POST", url)
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "multipart/form-data")
                .followRedirect(false)
                .setTimeout(30000) // 30 segundos de timeout
                .setMultipartParameter("nomeexi", nomeExibicao)
                .setMultipartParameter("desc", descricao)
                .setMultipartParameter("id", id)
                .setMultipartParameter("tipo", "android")
                .setMultipartFile("envft", foto)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e(TAG, "Erro na requisição: " + e.getMessage(), e);
                            callback.onCompleted(new Exception("Erro ao conectar com o servidor: " + e.getMessage()), null);
                            return;
                        }

                        Log.d(TAG, "=== RESPOSTA DO SERVIDOR (signup02.php) ===");
                        Log.d(TAG, "Resposta completa: " + (result != null ? result : "null"));

                        try {
                            JsonObject jsonResponse = JsonParser.parseString(result).getAsJsonObject();
                            Log.d(TAG, "Resposta em JSON: " + jsonResponse.toString());

                            if (jsonResponse.has("success") && jsonResponse.get("success").getAsBoolean()) {
                                Log.d(TAG, "Servidor confirmou sucesso");
                                context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                                        .edit()
                                        .putBoolean("cadastro_completo", true)
                                        .apply();
                                callback.onCompleted(null, "success");
                            } else {
                                String message = jsonResponse.has("message") ? jsonResponse.get("message").getAsString() : "Erro desconhecido";
                                Log.e(TAG, "Erro retornado pelo servidor: " + message);
                                callback.onCompleted(new Exception(message), null);
                            }
                        } catch (Exception jsonEx) {
                            Log.e(TAG, "Erro ao parsear JSON: " + jsonEx.getMessage(), jsonEx);
                            callback.onCompleted(new Exception("Resposta inválida do servidor"), null);
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

        // Prepara as duas versões do usuário (com e sem @)
        final String usuarioSemArroba = usuarioTemp.startsWith("@") ? usuarioTemp.substring(1) : usuarioTemp;
        final String usuarioComArroba = usuarioTemp.startsWith("@") ? usuarioTemp : "@" + usuarioTemp;

        String url = BASE_URL + "login_android.php";
        Log.d(TAG, "=== INÍCIO DO LOGIN ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Tentando primeiro com usuário: " + usuarioSemArroba);

        // Primeira tentativa - sem @
        tentarLogin(usuarioSemArroba, senha, new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                if (e != null || (result != null && !result.contains("success"))) {
                    // Se falhou, tenta com @
                    Log.d(TAG, "Primeira tentativa falhou, tentando com @: " + usuarioComArroba);
                    tentarLogin(usuarioComArroba, senha, callback);
                } else {
                    // Se deu certo, retorna o sucesso
                    callback.onCompleted(null, result);
                }
            }
        });
    }

    private void tentarLogin(String usuario, String senha, FutureCallback<String> callback) {
        Log.d(TAG, "Tentando login com usuário: " + usuario);
        Log.d(TAG, "Senha (tamanho): " + senha.length());

        Ion.with(context)
                .load("POST", BASE_URL + "login_android.php")
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Accept", "*/*")
                .setTimeout(30000)
                .setBodyParameter("usuario", usuario)
                .setBodyParameter("senha", senha)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e(TAG, "Erro na requisição: ", e);
                            callback.onCompleted(e, null);
                            return;
                        }

                        Log.d(TAG, "Resposta do login: " + result);

                        if (result != null) {
                            try {
                                JsonObject jsonResponse = JsonParser.parseString(result).getAsJsonObject();
                                if (jsonResponse.has("sucesso") && jsonResponse.get("sucesso").getAsBoolean()) {
                                    Log.d(TAG, "Login realizado com sucesso!");
                                    callback.onCompleted(null, "success");
                                    return;
                                }
                            } catch (Exception jsonEx) {
                                Log.d(TAG, "Resposta não é JSON válido, verificando string");
                            }

                            if (result.contains("success")) {
                                Log.d(TAG, "Login realizado com sucesso via string!");
                                callback.onCompleted(null, "success");
                            } else {
                                callback.onCompleted(new Exception("Login falhou"), null);
                            }
                        } else {
                            callback.onCompleted(new Exception("Resposta nula do servidor"), null);
                        }
                    }
                });
    }
}