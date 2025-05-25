package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
        String nomeExibicao = nomeEditText.getText().toString().trim();
        String descricao = descEditText.getText().toString().trim();

        if (nomeExibicao.isEmpty()) {
            nomeEditText.setError("Por favor, insira um nome de exibição");
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Por favor, selecione uma foto de perfil", Toast.LENGTH_SHORT).show();
            return;
        }

        // Converte a Uri da imagem para File
        try {
            File photoFile = createTempFileFromUri(selectedImageUri);
            
            // Chama o ApiService para completar o cadastro
            apiService.completarCadastro(nomeExibicao, descricao, photoFile, new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {
                    runOnUiThread(() -> {
                        if (e != null) {
                            Toast.makeText(Perfil.this, 
                                "Erro ao salvar perfil: " + e.getMessage(), 
                                Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (result != null && result.contains("success")) {
                            Toast.makeText(Perfil.this, 
                                "Cadastro concluído com sucesso!", 
                                Toast.LENGTH_LONG).show();
                            
                            // Redireciona para a tela inicial (tela1.xml)
                            Intent intent = new Intent(Perfil.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish(); // Fecha a activity atual
                        } else {
                            Toast.makeText(Perfil.this, 
                                "Erro ao salvar perfil. Tente novamente.", 
                                Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao processar a imagem: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }

    private File createTempFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("profile", ".jpg", getCacheDir());
        
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        
        outputStream.close();
        inputStream.close();
        
        return tempFile;
    }
}