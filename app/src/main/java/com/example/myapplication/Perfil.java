package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

        File photoFile = null;
        if (selectedImageUri != null) {
            try {
                Log.d(TAG, "Tentando processar a foto...");
                photoFile = createTempFileFromUri(selectedImageUri);
                Log.d(TAG, "Foto processada com sucesso: " + photoFile.getAbsolutePath());
            } catch (Exception e) {
                Log.e(TAG, "Erro ao processar imagem: " + e.getMessage(), e);
                Toast.makeText(this, "Erro ao processar imagem: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            }
        }
        
        // Mostra um progresso para o usuário
        Toast.makeText(this, "Salvando perfil...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Iniciando chamada para completarCadastro");
        
        // Chama o ApiService para completar o cadastro
        apiService.completarCadastro(nomeExibicao, descricao, photoFile, new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                Log.d(TAG, "Callback do completarCadastro recebido");
                Log.d(TAG, "Erro: " + (e != null ? e.getMessage() : "null"));
                Log.d(TAG, "Resultado: " + (result != null ? result : "null"));
                
                runOnUiThread(() -> {
                    if (e != null) {
                        Log.e(TAG, "Erro ao salvar perfil", e);
                        Toast.makeText(Perfil.this,
                            "Erro ao salvar perfil: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (result != null && result.contains("success")) {
                        Log.d(TAG, "Cadastro concluído com sucesso, preparando redirecionamento");
                        Toast.makeText(Perfil.this,
                            "Cadastro concluído com sucesso! Por favor, faça login.",
                            Toast.LENGTH_LONG).show();

                        // Pequeno delay para garantir que o Toast seja visto
                        new android.os.Handler().postDelayed(() -> {
                            try {
                                Log.d(TAG, "Iniciando redirecionamento para Login");
                                // Inicia o Login e limpa a pilha de activities
                                Intent intent = new Intent(Perfil.this, Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
                                             Intent.FLAG_ACTIVITY_NEW_TASK | 
                                             Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finishAffinity();
                            } catch (Exception ex) {
                                Log.e(TAG, "Erro ao redirecionar", ex);
                                Toast.makeText(Perfil.this,
                                    "Erro ao redirecionar: " + ex.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            }
                        }, 1000);
                    } else {
                        Log.e(TAG, "Erro ao salvar perfil. Resultado inválido: " + result);
                        Toast.makeText(Perfil.this,
                            "Erro ao salvar perfil. Tente novamente.",
                            Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
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