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
    private static final String BASE_URL = "http://tcc3yetecgrupo3t2.hospedagemdesites.ws/php/";;

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
                            try {
                                JsonObject jsonResponse = JsonParser.parseString(result).getAsJsonObject();
                                if (jsonResponse.has("id")) {
                                    String idFromServer = jsonResponse.get("id").getAsString();
                                    saveUserId(idFromServer);
                                } else {
                                    saveUserId(finalId);
                                }
                            } catch (Exception ex) {
                                Log.e(TAG, "Erro ao extrair ID do JSON: " + ex.getMessage());
                                saveUserId(finalId);
                            }
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

        String savedId = context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                .getString("user_id", null);
        Log.d(TAG, "ID salvo com sucesso? " + (savedId != null && savedId.equals(id)));
    }

    public void completarCadastro(String nomeExibicao, String descricao, File foto, FutureCallback<String> callback) {
        Log.d(TAG, "=== INÍCIO DO COMPLETAR CADASTRO (DETALHADO) ===");
        Log.d(TAG, "Verificando conexão com URL: " + BASE_URL + "signup02.php");

        Ion.with(context)
                .load("GET", BASE_URL + "signup02.php")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e(TAG, "ERRO NO TESTE DE CONEXÃO: " + e.getMessage());
                            callback.onCompleted(new Exception("Erro ao conectar com o servidor: " + e.getMessage()), null);
                            return;
                        }
                        Log.d(TAG, "TESTE DE CONEXÃO OK - Iniciando envio dos dados");
                        enviarDadosPerfil(nomeExibicao, descricao, foto, callback);
                    }
                });
    }

    private void enviarDadosPerfil(String nomeExibicao, String descricao, File foto, FutureCallback<String> callback) {
        String id = context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                .getString("user_id", null);

        Log.d(TAG, "=== DADOS A SEREM ENVIADOS ===");
        Log.d(TAG, "Nome de Exibição: " + nomeExibicao);
        Log.d(TAG, "Descrição: " + descricao);
        Log.d(TAG, "ID do Usuário: " + id);

        if (id == null) {
            Log.e(TAG, "ID do usuário não encontrado nas SharedPreferences");
            callback.onCompleted(new Exception("ID do usuário não encontrado"), null);
            return;
        }

        // Verificar se a foto é nula
        if (foto == null) {
            Log.e(TAG, "Arquivo de foto é nulo");
            callback.onCompleted(new Exception("Arquivo de foto não fornecido"), null);
            return;
        }

        // Verificar se o arquivo existe
        if (!foto.exists()) {
            Log.e(TAG, "Arquivo de foto não existe no caminho: " + foto.getAbsolutePath());
            callback.onCompleted(new Exception("Arquivo de foto não encontrado"), null);
            return;
        }

        Log.d(TAG, "Detalhes da Foto:");
        Log.d(TAG, "- Caminho: " + foto.getAbsolutePath());
        Log.d(TAG, "- Tamanho: " + foto.length() + " bytes");
        Log.d(TAG, "- Pode ler: " + foto.canRead());
        Log.d(TAG, "- Existe: " + foto.exists());

        String url = BASE_URL + "signup02.php";
        Log.d(TAG, "Iniciando requisição POST para: " + url);

        Ion.with(context)
                .load("POST", url)
                .setHeader("Accept", "application/json")
                .setLogging("IonLog", Log.DEBUG)
                .followRedirect(true)
                .setTimeout(30000)
                .setMultipartParameter("nomeexi", nomeExibicao)
                .setMultipartParameter("desc", descricao)
                .setMultipartParameter("id", id)
                .setMultipartFile("envft", foto)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e(TAG, "ERRO NA REQUISIÇÃO: ", e);
                            Log.e(TAG, "Tipo de erro: " + e.getClass().getSimpleName());
                            Log.e(TAG, "Mensagem de erro: " + e.getMessage());
                            if (e.getCause() != null) {
                                Log.e(TAG, "Causa do erro: " + e.getCause().getMessage());
                            }
                            callback.onCompleted(e, null);
                            return;
                        }

                        Log.d(TAG, "RESPOSTA DO SERVIDOR: " + result);

                        try {
                            JsonObject jsonResponse = JsonParser.parseString(result).getAsJsonObject();
                            if (jsonResponse.has("success") && jsonResponse.get("success").getAsBoolean()) {
                                Log.d(TAG, "Perfil criado com sucesso!");
                                context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                                        .edit()
                                        .putBoolean("cadastro_completo", true)
                                        .apply();
                                callback.onCompleted(null, "success");
                            } else {
                                String message = jsonResponse.has("message") ?
                                        jsonResponse.get("message").getAsString() : "Erro desconhecido";
                                Log.e(TAG, "Erro retornado pelo servidor: " + message);
                                callback.onCompleted(new Exception(message), null);
                            }
                        } catch (Exception jsonEx) {
                            Log.e(TAG, "Erro ao processar resposta do servidor: " + jsonEx.getMessage());
                            Log.e(TAG, "Resposta recebida: " + result);
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
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf(Math.random());

        String timestampPart = timestamp.substring(Math.max(0, timestamp.length() - 6));

        String randomPart = random.substring(2);
        if (randomPart.length() > 6) {
            randomPart = randomPart.substring(0, 6);
        } else {
            while (randomPart.length() < 6) {
                randomPart += "0";
            }
        }

        return timestampPart + randomPart;
    }

    public void realizarLogin(String usuarioOriginal, String senha, FutureCallback<String> callback) {
        String usuarioTemp = usuarioOriginal.replace(" ", "").toLowerCase();

        Log.d(TAG, "=== DETALHES DO LOGIN ===");
        Log.d(TAG, "Usuário original: " + usuarioOriginal);
        Log.d(TAG, "Usuário após limpar espaços: " + usuarioTemp);

        final String usuarioSemArroba = usuarioTemp.startsWith("@") ? usuarioTemp.substring(1) : usuarioTemp;
        final String usuarioComArroba = usuarioTemp.startsWith("@") ? usuarioTemp : "@" + usuarioTemp;

        String url = BASE_URL + "login_android.php";
        Log.d(TAG, "=== INÍCIO DO LOGIN ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Tentando primeiro com usuário: " + usuarioSemArroba);

        tentarLogin(usuarioSemArroba, senha, new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                if (e != null || (result != null && !result.contains("success"))) {
                    Log.d(TAG, "Primeira tentativa falhou, tentando com @: " + usuarioComArroba);
                    tentarLogin(usuarioComArroba, senha, callback);
                } else {
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
                .setCallback(new FutureCallback<String>()
                {
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

    public void buscarPerfil(String id, com.koushikdutta.async.future.FutureCallback<String> callback) {
        String url = BASE_URL + "receber_perfil.php?id=" + id;
        Ion.with(context)
            .load("GET", url)
            .asString()
            .setCallback(callback);
    }
}