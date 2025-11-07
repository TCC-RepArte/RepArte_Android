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
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.myapplication.api.ApiService;
import com.koushikdutta.async.future.FutureCallback;
import org.json.JSONObject;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Alt_perfil extends AppCompatActivity {
    private static final String TAG = "Alt_perfil";
    private ImageView profileImageView;
    private Button btnFoto;
    private EditText nomeEditText;
    private EditText descEditText;
    private Button salvarButton;
    private LinearLayout btnVoltar;
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
        setContentView(R.layout.alt_perfil);

        // Inicializa o ApiService
        apiService = new ApiService(this);

        // Inicializa as views
        profileImageView = findViewById(R.id.profile);
        btnFoto = findViewById(R.id.btn_foto);
        nomeEditText = findViewById(R.id.nome);
        descEditText = findViewById(R.id.desc);
        salvarButton = findViewById(R.id.btnSalvar);
        btnVoltar = findViewById(R.id.btn_Voltar);

        // Recupera o id do usuário das SharedPreferences
        String userId = getSharedPreferences("RepArte", MODE_PRIVATE).getString("user_id", null);

        if (userId != null) {
            apiService.buscarPerfil(userId, (e, result) -> {
                runOnUiThread(() -> {
                    if (e != null) {
                        Toast.makeText(this, "Erro ao buscar perfil", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.getBoolean("success")) {
                            nomeEditText.setText(json.getString("nome"));
                            descEditText.setText(json.getString("descricao"));

                            // Carregar a foto do perfil usando Ion com logs para debug

                            String fotoUrl = apiService.getFotoPerfilUrl(userId);
                            Log.d("Perfil", "URL da foto: " + fotoUrl);
                            Ion.with(this)
                                    .load(fotoUrl)
                                    .intoImageView(profileImageView)
                                    .setCallback((exception, imageView) -> {
                                        if (exception != null) {
                                            Log.e("Perfil", "Erro ao carregar imagem", exception);
                                        } else {
                                            Log.d("Perfil", "Imagem carregada com sucesso");
                                        }
                                    });
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            });
        }

        // Configura o botão de selecionar foto
        btnFoto.setOnClickListener(v -> pickImage.launch("image/*"));

        // Configura o botão de salvar
        salvarButton.setOnClickListener(v -> salvarPerfil());

        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(Alt_perfil.this, ConfActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });


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
                Toast.makeText(this, "Erro ao processar imagem: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        }

        salvarButton.setEnabled(false);
        Toast.makeText(this, "Salvando perfil...", Toast.LENGTH_SHORT).show();

        apiService.completarCadastro(nomeExibicao, descricao, photoFile, new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                runOnUiThread(() -> {
                    salvarButton.setEnabled(true);

                    if (e != null) {
                        Log.e(TAG, "Erro ao salvar perfil: " + e.getMessage(), e);
                        new androidx.appcompat.app.AlertDialog.Builder(Alt_perfil.this)
                                .setTitle("Erro ao Salvar Perfil")
                                .setMessage("Ocorreu um erro: " + e.getMessage() + "\nDeseja tentar novamente?")
                                .setPositiveButton("Tentar Novamente", (dialog, which) -> salvarPerfil())
                                .setNegativeButton("Voltar", (dialog, which) -> {
                                    Intent intent = new Intent(Alt_perfil.this, Tela.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                })
                                .setCancelable(false)
                                .show();
                        return;
                    }

                    if (result != null && result.equals("success")) {
                        Log.d(TAG, "Perfil salvo com sucesso!");

                        Intent intent = new Intent(Alt_perfil.this, Tela.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        Log.e(TAG, "Erro ao salvar perfil: resposta inesperada do servidor");
                        new androidx.appcompat.app.AlertDialog.Builder(Alt_perfil.this)
                                .setTitle("Erro ao Salvar Perfil")
                                .setMessage("O servidor retornou uma resposta inesperada. Deseja tentar novamente?")
                                .setPositiveButton("Tentar Novamente", (dialog, which) -> salvarPerfil())
                                .setNegativeButton("Voltar para Tela inicial", (dialog, which) -> {
                                    Intent intent = new Intent(Alt_perfil.this, Tela.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                })
                                .setCancelable(false)
                                .show();
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

