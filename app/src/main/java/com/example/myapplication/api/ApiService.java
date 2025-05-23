package com.example.myapplication.api;

import android.content.Context;
import android.util.Log;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.async.future.FutureCallback;

public class ApiService {
    private static final String TAG = "ApiService";
    // Removendo o /RepArte/ do caminho já que pode estar incorreto
    private static final String BASE_URL = "http://tcc3yetecgrupo3t2.hospedagemdesites.ws/";
    private Context context;

    public ApiService(Context context) {
        this.context = context;
    }

    public void cadastrarUsuario(String usuario, String senha, FutureCallback<JsonObject> callback) {
        // Remove espaços do nome de usuário
        usuario = usuario.replace(" ", "");
        
        String url = BASE_URL + "inserirt.php";
        Log.d(TAG, "=== INÍCIO DO CADASTRO ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Dados enviados - Usuário: " + usuario);
        Log.d(TAG, "Dados enviados - Senha: " + senha);
        
        Ion.with(context)
                .load("POST", url)
                .setBodyParameter("usuario", usuario)
                .setBodyParameter("senha", senha)
                .asString()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Log.e(TAG, "=== ERRO NA REQUISIÇÃO ===");
                        Log.e(TAG, "Tipo de erro: " + e.getClass().getSimpleName());
                        Log.e(TAG, "Mensagem de erro: " + e.getMessage());
                        Log.e(TAG, "Stack trace completo: ", e);
                        callback.onCompleted(e, null);
                        return;
                    }

                    if (result != null) {
                        Log.d(TAG, "=== RESPOSTA DO SERVIDOR (RAW) ===");
                        Log.d(TAG, "Resposta bruta: " + result);
                        
                        try {
                            // Tenta converter a string para JSON
                            JsonObject jsonObject = Ion.with(context)
                                    .load("GET", "about:blank")
                                    .setStringBody(result)
                                    .asJsonObject()
                                    .get();
                            
                            callback.onCompleted(null, jsonObject);
                        } catch (Exception jsonError) {
                            Log.e(TAG, "Erro ao converter resposta para JSON: " + jsonError.getMessage());
                            Log.e(TAG, "Resposta recebida: " + result);
                            callback.onCompleted(jsonError, null);
                        }
                    } else {
                        Log.e(TAG, "Resposta nula do servidor");
                        callback.onCompleted(new Exception("Resposta nula do servidor"), null);
                    }
                });
    }

    public void realizarLogin(String usuario, String senha, FutureCallback<JsonObject> callback) {
        // Remove espaços do nome de usuário
        usuario = usuario.replace(" ", "");
        
        String url = BASE_URL + "login.php";
        Log.d(TAG, "=== INÍCIO DO LOGIN ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Dados enviados - Usuário: " + usuario);
        Log.d(TAG, "Dados enviados - Senha: " + senha);

        Ion.with(context)
                .load("POST", url)
                .setBodyParameter("usuario", usuario)
                .setBodyParameter("senha", senha)
                .asString()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Log.e(TAG, "=== ERRO NO LOGIN ===");
                        Log.e(TAG, "Tipo de erro: " + e.getClass().getSimpleName());
                        Log.e(TAG, "Mensagem de erro: " + e.getMessage());
                        Log.e(TAG, "Stack trace completo: ", e);
                        callback.onCompleted(e, null);
                        return;
                    }

                    if (result != null) {
                        Log.d(TAG, "=== RESPOSTA DO SERVIDOR (RAW) ===");
                        Log.d(TAG, "Resposta bruta: " + result);
                        
                        try {
                            // Tenta converter a string para JSON
                            JsonObject jsonObject = Ion.with(context)
                                    .load("GET", "about:blank")
                                    .setStringBody(result)
                                    .asJsonObject()
                                    .get();
                            
                            callback.onCompleted(null, jsonObject);
                        } catch (Exception jsonError) {
                            Log.e(TAG, "Erro ao converter resposta para JSON: " + jsonError.getMessage());
                            Log.e(TAG, "Resposta recebida: " + result);
                            callback.onCompleted(jsonError, null);
                        }
                    } else {
                        Log.e(TAG, "Resposta nula do servidor");
                        callback.onCompleted(new Exception("Resposta nula do servidor"), null);
                    }
                });
    }
}