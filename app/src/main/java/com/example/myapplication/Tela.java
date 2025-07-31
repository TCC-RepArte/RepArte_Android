package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.api.ApiService;
import com.koushikdutta.ion.bitmap.Transform;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

public class Tela extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela1);

        apiService = new ApiService(this);

        //declarar
        ImageView perfil;
        SearchView pesquisar;

        //id
        perfil = findViewById(R.id.perfil);
        pesquisar = findViewById(R.id.pesquisar);

        // Anima os elementos da tela principal
        animateMainScreenElements(perfil, pesquisar);

        //evento do botao perfil
        perfil.setOnClickListener(view -> {
            // Aplica animação de clique
            AppAnimationUtils.animateButtonClick(perfil, () -> {
                Intent intent = new Intent(Tela.this, ConfActivity.class);
                startActivity(intent);
            });
        });

        //evento da barra de pesquisa
        pesquisar.setOnClickListener(view -> {
            // Aplica animação de clique
            AppAnimationUtils.animateButtonClick(pesquisar, () -> {
                // Navegar para a tela de busca
                Intent intent = new Intent(Tela.this, BuscaActivity.class);
                startActivity(intent);
            });
        });

        // Configurar a SearchView para abrir a tela de busca quando clicar
        pesquisar.setOnSearchClickListener(view -> {
            // Aplica animação de bounce para feedback
            AppAnimationUtils.animateBounce(pesquisar);
            Intent intent = new Intent(Tela.this, BuscaActivity.class);
            startActivity(intent);
        });

        // Opcional: Configurar para buscar quando submeter
        pesquisar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    Intent intent = new Intent(Tela.this, BuscaActivity.class);
                    intent.putExtra("termo_busca", query.trim());
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Busca em tempo real (opcional)
                return true;
            }
        });

        String userId = getSharedPreferences("RepArte", MODE_PRIVATE).getString("user_id", null);

        if (userId != null) {
            String fotoUrl = apiService.getFotoPerfilUrl(userId);
            Log.d("Perfil", "URL da foto: " + fotoUrl);

            Ion.with(this)
                    .load(fotoUrl)
                    .withBitmap()
                    .placeholder(R.drawable.user_white)
                    .error(R.drawable.user_white)
                    .transform(new Transform() {
                        @Override
                        public Bitmap transform(Bitmap b) {

                            return ImageUtil.createCircleBitmap(b);
                        }

                        @Override
                        public String key() {
                            return "circleTransformProfile";
                        }
                    })
                    .intoImageView(perfil)
                    .setCallback((exception, result) -> {
                        if (exception != null) {
                            Log.e("Perfil", "Erro ao carregar imagem circular", exception);
                        } else {
                            Log.d("Perfil", "Imagem circular carregada com sucesso");
                        }
                    });
        } else {
            Log.e("Perfil", "User ID é nulo, não foi possível carregar a foto.");
            perfil.setImageResource(R.drawable.user_white);
        }
    }

    /**
     * Anima os elementos da tela principal
     */
    private void animateMainScreenElements(ImageView perfil, SearchView pesquisar) {
        // Anima o perfil com fade in e escala
        if (perfil != null) {
            perfil.setAlpha(0f);
            perfil.setScaleX(0.8f);
            perfil.setScaleY(0.8f);
            perfil.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(800)
                    .setStartDelay(200)
                    .start();
        }

        // Anima a barra de pesquisa com slide up
        if (pesquisar != null) {
            pesquisar.setAlpha(0f);
            pesquisar.setTranslationY(30f);
            pesquisar.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(400)
                    .start();
        }

        // Anima outros elementos da tela se existirem
        // Você pode adicionar mais elementos aqui conforme necessário
    }
}



