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

        // Limpar sessão anterior para evitar conflitos
        limparSessao();

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
    
    public void limparSessao() {
        Log.d(TAG, "Limpando sessão do usuário");
        context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                .edit()
                .remove("user_id")
                .remove("ultimo_usuario")
                .remove("ultima_senha")
                .remove("cadastro_completo")
                .apply();
    }
    
    public String getUserIdAtual() {
        return context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                .getString("user_id", null);
    }
    
    public boolean isUsuarioLogado() {
        return getUserIdAtual() != null;
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
        
        // Limpar sessão anterior para evitar conflitos
        limparSessao();

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

    public void solicitarRecuperacaoSenha(String emailOuUsuario, FutureCallback<String> callback) {
        String alvo = emailOuUsuario == null ? "" : emailOuUsuario.trim();
        if (alvo.isEmpty()) {
            callback.onCompleted(new Exception("Informe seu e-mail ou usuário"), null);
            return;
        }

        String url = BASE_URL + "forgot_password.php";
        Log.d(TAG, "Solicitando recuperação de senha para: " + alvo);

        Ion.with(context)
                .load("POST", url)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setTimeout(20000)
                .setBodyParameter("alvo", alvo)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            callback.onCompleted(e, null);
                            return;
                        }
                        try {
                            if (result != null) {
                                JsonObject json = JsonParser.parseString(result).getAsJsonObject();
                                boolean ok = (json.has("success") && json.get("success").getAsBoolean())
                                        || result.toLowerCase().contains("success");
                                if (ok) {
                                    callback.onCompleted(null, "success");
                                    return;
                                }
                            }
                        } catch (Exception ignored) { }
                        callback.onCompleted(new Exception("Falha ao solicitar recuperação"), null);
                    }
                });
    }

    // Fluxo de recuperação: enviar código para email/usuário
    public void enviarCodigoRecuperacao(String emailOuUsuario, FutureCallback<String> callback) {
        String alvo = emailOuUsuario == null ? "" : emailOuUsuario.trim();
        if (alvo.isEmpty()) {
            callback.onCompleted(new Exception("Informe seu e-mail ou usuário"), null);
            return;
        }
        Ion.with(context)
                .load("POST", BASE_URL + "forgot_send_code.php")
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setTimeout(20000)
                .setBodyParameter("alvo", alvo)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) { callback.onCompleted(e, null); return; }
                        callback.onCompleted(null, result != null ? result : "success");
                    }
                });
    }

    // Fluxo de recuperação: validar código e obter token
    public void verificarCodigoRecuperacao(String emailOuUsuario, String codigo, FutureCallback<String> callback) {
        String alvo = emailOuUsuario == null ? "" : emailOuUsuario.trim();
        if (alvo.isEmpty() || codigo == null || codigo.trim().isEmpty()) {
            callback.onCompleted(new Exception("Dados incompletos"), null);
            return;
        }
        Ion.with(context)
                .load("POST", BASE_URL + "forgot_verify_code.php")
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setTimeout(20000)
                .setBodyParameter("alvo", alvo)
                .setBodyParameter("codigo", codigo)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) { callback.onCompleted(e, null); return; }
                        try {
                            JsonObject json = JsonParser.parseString(result).getAsJsonObject();
                            if (json.has("token")) {
                                callback.onCompleted(null, json.get("token").getAsString());
                                return;
                            }
                        } catch (Exception ignore) {}
                        callback.onCompleted(new Exception("Código inválido"), null);
                    }
                });
    }

    // Fluxo de recuperação: redefinir senha de fato
    public void redefinirSenha(String emailOuUsuario, String token, String novaSenha, FutureCallback<String> callback) {
        String alvo = emailOuUsuario == null ? "" : emailOuUsuario.trim();
        if (alvo.isEmpty() || token == null || token.trim().isEmpty() || novaSenha == null || novaSenha.isEmpty()) {
            callback.onCompleted(new Exception("Dados incompletos"), null);
            return;
        }
        Ion.with(context)
                .load("POST", BASE_URL + "forgot_reset_password.php")
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setTimeout(20000)
                .setBodyParameter("alvo", alvo)
                .setBodyParameter("token", token)
                .setBodyParameter("senha", novaSenha)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) { callback.onCompleted(e, null); return; }
                        boolean ok = result != null && result.toLowerCase().contains("success");
                        callback.onCompleted(ok ? null : new Exception("Falha ao redefinir"), ok ? "success" : null);
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
                                    
                                    // Extrair e salvar o ID do usuário se disponível
                                    if (jsonResponse.has("id")) {
                                        String userId = jsonResponse.get("id").getAsString();
                                        saveUserId(userId);
                                        Log.d(TAG, "ID do usuário salvo: " + userId);
                                    } else if (jsonResponse.has("user_id")) {
                                        String userId = jsonResponse.get("user_id").getAsString();
                                        saveUserId(userId);
                                        Log.d(TAG, "ID do usuário salvo: " + userId);
                                    }
                                    
                                    callback.onCompleted(null, "success");
                                    return;
                                }
                            } catch (Exception jsonEx) {
                                Log.d(TAG, "Resposta não é JSON válido, verificando string");
                            }

                            if (result.contains("success")) {
                                Log.d(TAG, "Login realizado com sucesso via string!");
                                
                                // Tentar extrair ID mesmo de resposta string
                                try {
                                    if (result.contains("id:")) {
                                        String[] parts = result.split("id:");
                                        if (parts.length > 1) {
                                            String userId = parts[1].trim().split("\\s")[0];
                                            saveUserId(userId);
                                            Log.d(TAG, "ID do usuário extraído da string: " + userId);
                                        }
                                    }
                                } catch (Exception jsonEx) {
                                    Log.d(TAG, "Não foi possível extrair ID da resposta string");
                                }
                                
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
                .setHeader("Accept", "application/json")
                .setHeader("Connection", "close")
                .setTimeout(30000)
                .followRedirect(true)
                .asString()
                .setCallback(callback);
    }

    public void buscarUsuarioPorId(String id, com.koushikdutta.async.future.FutureCallback<String> callback) {
        String url = BASE_URL + "receber_user.php?id=" + id;
        Log.d(TAG, "=== BUSCANDO USUÁRIO POR ID ===");
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "ID: " + id);

        Ion.with(context)
                .load("GET", url)
                .setHeader("Accept", "application/json")
                .setHeader("Connection", "close")
                .setTimeout(30000)
                .followRedirect(true)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e(TAG, "Erro ao buscar usuário: ", e);
                            callback.onCompleted(e, null);
                            return;
                        }
                        Log.d(TAG, "Resposta receber_usuario: " + result);
                        callback.onCompleted(null, result);
                    }
                });
    }

    public String getFotoPerfilUrl(String userId) {
        return BASE_URL + "receber_foto.php?id=" + userId;
    }

    public void buscarTodasPostagens(FutureCallback<String> callback) {
        String url = BASE_URL + "buscar_todas_postagens.php";
        Log.d(TAG, "=== BUSCANDO TODAS AS POSTAGENS ===");
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "Iniciando requisição GET...");

        Ion.with(context)
                .load("GET", url)
                .setHeader("Accept", "application/json")
                .setHeader("Connection", "close")
                .setTimeout(30000)
                .followRedirect(true)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.d(TAG, "=== CALLBACK RECEBIDO ===");
                        Log.d(TAG, "Tempo da requisição: " + System.currentTimeMillis());
                        
                        if (e != null) {
                            Log.e(TAG, "Erro ao buscar postagens: ", e);
                            Log.e(TAG, "Tipo de erro: " + e.getClass().getSimpleName());
                            Log.e(TAG, "Mensagem: " + e.getMessage());
                            if (e.getCause() != null) {
                                Log.e(TAG, "Causa: " + e.getCause().getMessage());
                            }
                            callback.onCompleted(e, null);
                            return;
                        }

                        if (result == null) {
                            Log.e(TAG, "Resposta é NULL!");
                            Log.e(TAG, "O servidor não retornou nenhum dado");
                            callback.onCompleted(new Exception("Resposta nula do servidor"), null);
                            return;
                        }

                        if (result.isEmpty()) {
                            Log.w(TAG, "Resposta vazia do servidor (tamanho: 0)");
                            Log.w(TAG, "Isso indica que o PHP pode estar retornando string vazia");
                            Log.w(TAG, "Verifique se há postagens no banco de dados");
                            Log.w(TAG, "Ou se há erros no arquivo PHP que não estão sendo reportados");
                            callback.onCompleted(null, result);
                            return;
                        }

                        // Verificar se a resposta parece ser HTML (erro do servidor)
                        if (result.trim().startsWith("<")) {
                            Log.e(TAG, "Resposta parece ser HTML ao invés de JSON!");
                            Log.e(TAG, "Primeiros 200 caracteres: " + result.substring(0, Math.min(200, result.length())));
                            callback.onCompleted(new Exception("Servidor retornou HTML ao invés de JSON"), null);
                            return;
                        }

                        Log.d(TAG, "Resposta recebida (tamanho: " + result.length() + " caracteres)");
                        Log.d(TAG, "Primeiros 500 caracteres: " + result.substring(0, Math.min(500, result.length())));
                        
                        // Verificar se começa com JSON válido
                        String trimmed = result.trim();
                        if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
                            Log.w(TAG, "Resposta não parece ser JSON válido (não começa com { ou [)");
                            Log.w(TAG, "Primeiros caracteres: " + trimmed.substring(0, Math.min(50, trimmed.length())));
                        }
                        
                        callback.onCompleted(null, result);
                    }
                });
    }

    public void buscarPostagensUsuario(String userId, FutureCallback<String> callback) {
        if (userId == null) {
            callback.onCompleted(new Exception("ID do usuário não fornecido"), null);
            return;
        }

        String url = BASE_URL + "buscar_postagens_usuario.php?user_id=" + userId;
        Log.d(TAG, "=== BUSCANDO POSTAGENS DO USUÁRIO ===");
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "ID do Usuário: " + userId);

        Ion.with(context)
                .load("GET", url)
                .setHeader("Accept", "application/json")
                .setHeader("Connection", "close")
                .setTimeout(30000)
                .followRedirect(true)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e(TAG, "Erro ao buscar postagens: ", e);
                            callback.onCompleted(e, null);
                            return;
                        }

                        Log.d(TAG, "Postagens encontradas: " + result);
                        callback.onCompleted(null, result);
                    }
                });
    }

    public void enviarPost(String titulo, String texto, int idObra, FutureCallback<String> callback) {
        String userId = context.getSharedPreferences("RepArte", Context.MODE_PRIVATE)
                .getString("user_id", null);

        if (userId == null) {
            callback.onCompleted(new Exception("Usuário não autenticado"), null);
            return;
        }

        String url = BASE_URL + "criar_postagem.php";
        Log.d(TAG, "=== ENVIANDO POST ===");
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "Título: " + titulo);
        Log.d(TAG, "Texto: " + texto);
        Log.d(TAG, "ID da Obra: " + idObra);
        Log.d(TAG, "ID do Usuário: " + userId);

        Ion.with(context)
                .load("POST", url)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Connection", "close")
                .setTimeout(30000)
                .setBodyParameter("titulo", titulo)
                .setBodyParameter("texto", texto)
                .setBodyParameter("id_obra", String.valueOf(idObra))
                .setBodyParameter("id_usuario", userId)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e(TAG, "Erro ao enviar post: ", e);
                            callback.onCompleted(e, null);
                            return;
                        }

                        Log.d(TAG, "Resposta do envio do post: " + result);
                        if (result != null && result.toLowerCase().contains("success")) {
                            Log.d(TAG, "Post enviado com sucesso!");
                            callback.onCompleted(null, "success");
                        } else {
                            Log.e(TAG, "Erro no envio do post. Resposta: " + result);
                            callback.onCompleted(new Exception("Erro no envio: " + result), null);
                        }
                    }
                });
    }
}