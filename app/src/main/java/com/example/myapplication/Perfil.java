package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.myapplication.api.ApiService;
import com.koushikdutta.async.future.FutureCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Perfil extends AppCompatActivity {
    private static final String TAG = "Perfil";
    private ImageView profileImageView;
    private Button btnFoto;
    private EditText nomeEditText;
    private EditText descEditText;
    private Button salvarButton;
    private Uri selectedImageUri;
    private ApiService apiService;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    profileImageView.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil1);

        // Inicializa o ApiService
        apiService = new ApiService(this);

        // Inicializa as views
        profileImageView = findViewById(R.id.profile);
        btnFoto = findViewById(R.id.btn_foto);
        nomeEditText = findViewById(R.id.nome);
        descEditText = findViewById(R.id.desc);
        salvarButton = findViewById(R.id.salvar);

        // Configura o botão de selecionar foto
        btnFoto.setOnClickListener(v -> pickImage.launch("image/*"));

        // Configura o botão de salvar
        salvarButton.setOnClickListener(v -> salvarPerfil());
    }

    private void salvarPerfil() {
        Log.d(TAG, "=== INICIANDO SALVAMENTO DO PERFIL ===");
        String nomeExibicao = nomeEditText.getText().toString().trim();
        String descricao = descEditText.getText().toString().trim();

        Log.d(TAG, "Nome: " + nomeExibicao);
        Log.d(TAG, "Descrição: " + descricao);
        Log.d(TAG, "Tem foto selecionada: " + (selectedImageUri != null));

        if (nomeExibicao.isEmpty()) {
            nomeEditText.setError("Por favor, insira um nome de exibição");
            Log.d(TAG, "Nome de exibição vazio");
            return;
        }

        // Apenas navega para a tela de login, sem enviar nada para o banco
        Toast.makeText(this, "Cadastro concluído! Por favor, faça login.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Perfil.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }

    private File createTempFileFromUri(Uri uri) throws Exception {
        if (uri == null) {
            throw new Exception("Nenhuma imagem selecionada");
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                throw new Exception("Não foi possível acessar a imagem");
            }

            File tempFile = File.createTempFile("profile", ".jpg", getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            try {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                return tempFile;
            } finally {
                try {
                    outputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new Exception("Erro ao processar imagem: " + e.getMessage());
        }
    }
}