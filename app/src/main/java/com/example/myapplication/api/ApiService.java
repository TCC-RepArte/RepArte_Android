package com.example.myapplication.api;

import android.content.Context;
import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.async.future.FutureCallback;

public class ApiService {
    private static final String TAG = "ApiService";
    private static final String BASE_URL = "http://tcc3yetecgrupo3t2.hospedagemdesites.ws/ARQUIVOS/ARQUIVOS/";
    private Context context;

    public ApiService(Context context) {
        this.context = context;
    }

    public void cadastrarUsuario(String usuario, String email, String senha, FutureCallback<String> callback) {
        // Remove espaços do nome de usuário e email
        usuario = usuario.replace(" ", "");
        email = email.replace(" ", "");
        
        String url = BASE_URL + "inserirt.php";
        Log.d(TAG, "=== INÍCIO DO CADASTRO ===");
        Log.d(TAG, "URL completa: " + url);
        Log.d(TAG, "Dados enviados - Usuário: " + usuario);
        Log.d(TAG, "Dados enviados - Email: " + email);
        Log.d(TAG, "Dados enviados - Senha: " + senha);
        
        Ion.with(context)
                .load("POST", url)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("Accept", "*/*")
                .setBodyParameter("usuario", usuario)
                .setBodyParameter("email", email)
                .setBodyParameter("senha", senha)
                .asString()
                .setCallback(callback);
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