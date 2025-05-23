package com.example.myapplication.api;

import android.content.Context;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.async.future.FutureCallback;

public class ApiService {
    // Ajustando para usar localhost - substitua "seuprojeto" pelo nome da sua pasta no wamp
    private static final String BASE_URL = "http://10.0.2.2/RepArte/php_rosinha/php.rosinha/";
    private Context context;

    public ApiService(Context context) {
        this.context = context;
    }

    public void cadastrarUsuario(String usuario, String senha, FutureCallback<JsonObject> callback) {
        Ion.with(context)
                .load("POST", BASE_URL + "inserirt.php")
                .setBodyParameter("usuario", usuario)
                .setBodyParameter("senha", senha)
                .asJsonObject()
                .setCallback(callback);
    }

    public void realizarLogin(String usuario, String senha, FutureCallback<JsonObject> callback) {
        Ion.with(context)
                .load("POST", BASE_URL + "login.php")
                .setBodyParameter("usuario", usuario)
                .setBodyParameter("senha", senha)
                .asJsonObject()
                .setCallback(callback);
    }
}