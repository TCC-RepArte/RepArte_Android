package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button; // Para o botão Editar Perfil
import android.widget.ImageButton; // Para o botão Voltar
import android.widget.TextView;
import android.widget.ImageView;
import com.example.myapplication.api.ApiService;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.bitmap.Transform;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ExibirPerfil extends AppCompatActivity {

    private static final String TAG = "ExibirPerfil"; // Tag para Logs
    private ApiService apiService;
    private TextView textViewPostagem;
    private TextView textViewComentarios;
    private TextView textViewSobreMim;
    private ImageView profileImageView;
    private Button btnSalvar;
    private View indicatorView;
    private ConstraintLayout constraintLayout;

    private ImageButton btnVoltar;
    
    // Variáveis para postagens
    private RecyclerView recyclerViewPostagens;
    private PostagemAdapter postagemAdapter;
    private List<ModeloPostagem> listaPostagens;
    private String userIdAtual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exibir_perfil);

        apiService = new ApiService(this);

        constraintLayout = findViewById(R.id.main);
        textViewPostagem = findViewById(R.id.textViewPostagem);
        textViewComentarios = findViewById(R.id.textViewComentarios);
        textViewSobreMim = findViewById(R.id.textViewSobreMim);
        indicatorView = findViewById(R.id.indicatorView);
        profileImageView = findViewById(R.id.imageView4);
        btnSalvar = findViewById(R.id.btnSalvar);

        btnVoltar = findViewById(R.id.btn_Voltar);

        if (constraintLayout == null) {
            Log.e(TAG, "ConstraintLayout 'main' não encontrado!");
            return;
        }
        if (indicatorView == null) {
            Log.e(TAG, "IndicatorView 'indicatorView' não encontrado!");
        }

        userIdAtual = getSharedPreferences("RepArte", MODE_PRIVATE).getString("user_id", null);
        Log.d(TAG, "=== DEBUG PERFIL ===");
        Log.d(TAG, "User ID atual: " + userIdAtual);
        
        if (userIdAtual == null) {
            Log.e(TAG, "ERRO: User ID é nulo! Não será possível carregar postagens.");
        } else {
            Log.d(TAG, "User ID encontrado: " + userIdAtual);
        }
        
        String fotoUrl = apiService.getFotoPerfilUrl(userIdAtual);
        Log.d("Perfil", "URL da foto: " + fotoUrl);

            if (profileImageView != null) {
                Ion.with(this)
                        .load(fotoUrl)
                        .withBitmap()
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
                        .transform(new Transform() {
                            @Override
                            public Bitmap transform(Bitmap b) {
                                // Certifique-se que ImageUtil.createCircleBitmap(b) existe e funciona.
                                // Se não, remova esta transformação ou implemente a classe/método.
                                return ImageUtil.createCircleBitmap(b);
                            }

                            @Override
                            public String key() {
                                return "circleTransformProfile";
                            }
                        })
                        .intoImageView(profileImageView) // Usando a variável de membro profile_image
                        .setCallback((exception, result) -> {
                            if (exception != null) {
                                Log.e("Perfil", "Erro ao carregar imagem circular", exception);
                                profileImageView.setImageResource(R.drawable.user_white); // Fallback na variável de membro
                            } else {
                                Log.d("Perfil", "Imagem circular carregada com sucesso");
                            }
                        });
            } else {
                Log.e("Perfil", "ImageView profile_image é nula. Não foi possível carregar a foto.");
            }


        // Configura o botão de salvar com animação
        if (btnSalvar != null) {
            btnSalvar.setOnClickListener(v -> {
                // Aplica animação de clique
                AppAnimationUtils.animateButtonClick(btnSalvar, () -> {
                    Intent intent = new Intent(ExibirPerfil.this, Alt_perfil.class);
                    String currentUserId = getSharedPreferences("RepArte", MODE_PRIVATE).getString("user_id", null);
                    if (currentUserId != null) {
                        intent.putExtra("USER_ID_EXTRA", currentUserId); // Opcional: passa o ID para a tela de edição
                    }
                    startActivity(intent);
                });
            });
        } else {
            Log.e(TAG, "Botão 'btnSalvar' não encontrado para configurar o OnClickListener.");
        }

        btnVoltar.setOnClickListener(v -> {
            // Aplica animação de clique
            AppAnimationUtils.animateButtonClick(btnVoltar, () -> {
                Intent intent = new Intent(ExibirPerfil.this, ConfActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        });


        // Configura o estado inicial e listeners
        if (textViewPostagem != null) {
            if (indicatorView != null) {
                updateIndicator(textViewPostagem);
            }
            textViewPostagem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aplica animação de bounce para feedback visual
                    AppAnimationUtils.animateBounce(textViewPostagem);
                    updateIndicator(textViewPostagem);
                    // Mostrar postagens do usuário
                    mostrarPostagensUsuario();
                }
            });
        } else {
            Log.w(TAG, "TextView 'textViewPostagem' não encontrado!");
        }

        if (textViewComentarios != null) {
            textViewComentarios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aplica animação de bounce para feedback visual
                    AppAnimationUtils.animateBounce(textViewComentarios);
                    updateIndicator(textViewComentarios);
                    // TODO: Lógica para mostrar conteúdo de Comentários
                }
            });
        } else {
            Log.w(TAG, "TextView 'textViewComentarios' não encontrado!");
        }

        if (textViewSobreMim != null) {
            textViewSobreMim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aplica animação de bounce para feedback visual
                    AppAnimationUtils.animateBounce(textViewSobreMim);
                    updateIndicator(textViewSobreMim);
                    // TODO: Lógica para mostrar conteúdo de Sobre Mim
                }
            });
        } else {
            Log.w(TAG, "TextView 'textViewSobreMim' não encontrado!");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Aplica animações de entrada para os elementos principais
        animateElementsOnStart();
        
        // Inicializar RecyclerView para postagens
        inicializarRecyclerViewPostagens();
        
        // Carregar postagens do usuário atual
        mostrarPostagensUsuario();
    }
    
    private void inicializarRecyclerViewPostagens() {
        recyclerViewPostagens = findViewById(R.id.recycler_postagens_perfil);
        if (recyclerViewPostagens != null) {
            listaPostagens = new ArrayList<>();
            postagemAdapter = new PostagemAdapter(this, listaPostagens);
            
            recyclerViewPostagens.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewPostagens.setAdapter(postagemAdapter);
        }
    }
    
    private void mostrarPostagensUsuario() {
        Log.d(TAG, "=== MOSTRAR POSTAGENS USUÁRIO ===");
        Log.d(TAG, "User ID atual: " + userIdAtual);
        
        if (userIdAtual != null) {
            Log.d(TAG, "Chamando API para buscar postagens do usuário: " + userIdAtual);
            apiService.buscarPostagensUsuario(userIdAtual, new com.koushikdutta.async.future.FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {
                    runOnUiThread(() -> {
                        if (e != null) {
                            Log.e(TAG, "Erro ao carregar postagens: " + e.getMessage());
                            Toast.makeText(ExibirPerfil.this, "Erro ao carregar postagens", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        Log.d(TAG, "Resposta da API: " + result);
                        
                        if (result != null && !result.isEmpty()) {
                            try {
                                Log.d(TAG, "Tentando processar JSON da resposta...");
                                JSONArray jsonArray = new JSONArray(result);
                                Log.d(TAG, "JSON processado com sucesso. Número de postagens: " + jsonArray.length());
                                
                                List<ModeloPostagem> novasPostagens = new ArrayList<>();
                                
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject postagemJson = jsonArray.getJSONObject(i);
                                    ModeloPostagem postagem = new ModeloPostagem(
                                        postagemJson.getString("id"),
                                        postagemJson.getString("titulo"),
                                        postagemJson.getString("texto"),
                                        postagemJson.getString("id_usuario"),
                                        postagemJson.getString("nome_usuario"),
                                        postagemJson.getString("foto_usuario"),
                                        postagemJson.getString("id_obra"),
                                        postagemJson.getString("titulo_obra"),
                                        postagemJson.getString("poster_obra"),
                                        postagemJson.getString("data_criacao")
                                    );
                                    novasPostagens.add(postagem);
                                }
                                
                                Log.d(TAG, "Verificando se lista e adapter estão disponíveis...");
                                Log.d(TAG, "Lista postagens é nula? " + (listaPostagens == null));
                                Log.d(TAG, "Adapter é nulo? " + (postagemAdapter == null));
                                
                                if (listaPostagens != null && postagemAdapter != null) {
                                    Log.d(TAG, "Atualizando lista e adapter...");
                                    listaPostagens.clear();
                                    listaPostagens.addAll(novasPostagens);
                                    postagemAdapter.notifyDataSetChanged();
                                    
                                    Log.d(TAG, "Postagens do usuário carregadas: " + novasPostagens.size());
                                    Log.d(TAG, "Adapter notificado da mudança");
                                } else {
                                    Log.e(TAG, "ERRO: Lista ou adapter são nulos!");
                                }
                                
                            } catch (Exception jsonEx) {
                                Log.e(TAG, "Erro ao processar JSON das postagens: " + jsonEx.getMessage());
                            }
                        } else {
                            Log.d(TAG, "Nenhuma postagem encontrada para o usuário");
                        }
                    });
                }
            });
        }
    }

    /**
     * Aplica animações de entrada para os elementos principais da tela
     */
    private void animateElementsOnStart() {
        // Anima a imagem do perfil com fade in
        if (profileImageView != null) {
            AppAnimationUtils.animateFadeIn(profileImageView);
        }

        // Anima os botões com slide up
        if (btnSalvar != null) {
            btnSalvar.setVisibility(View.INVISIBLE);
            btnSalvar.postDelayed(() -> {
                btnSalvar.setVisibility(View.VISIBLE);
                AppAnimationUtils.animateSlideUp(btnSalvar);
            }, 200);
        }

        if (btnVoltar != null) {
            btnVoltar.setVisibility(View.INVISIBLE);
            btnVoltar.postDelayed(() -> {
                btnVoltar.setVisibility(View.VISIBLE);
                AppAnimationUtils.animateSlideUp(btnVoltar);
            }, 300);
        }

        // Anima os textos de navegação com fade in
        if (textViewPostagem != null) {
            textViewPostagem.setAlpha(0f);
            textViewPostagem.postDelayed(() -> {
                AppAnimationUtils.animateFadeInWithVisibility(textViewPostagem);
            }, 400);
        }

        if (textViewComentarios != null) {
            textViewComentarios.setAlpha(0f);
            textViewComentarios.postDelayed(() -> {
                AppAnimationUtils.animateFadeInWithVisibility(textViewComentarios);
            }, 500);
        }

        if (textViewSobreMim != null) {
            textViewSobreMim.setAlpha(0f);
            textViewSobreMim.postDelayed(() -> {
                AppAnimationUtils.animateFadeInWithVisibility(textViewSobreMim);
            }, 600);
        }
    }

    private void updateIndicator(TextView selectedTextView) {
        // Verificações de nulo para segurança
        if (indicatorView == null || selectedTextView == null || constraintLayout == null) {
            Log.w(TAG, "updateIndicator: Uma ou mais Views são nulas! Não é possível atualizar o indicador.");
            return;
        }

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        // 1. Conectar o TOPO do 'indicatorView' à BASE do 'selectedTextView'
        constraintSet.connect(
                indicatorView.getId(),
                ConstraintSet.TOP,
                selectedTextView.getId(),
                ConstraintSet.BOTTOM,
                dpToPx(4) // Sua margem de 4dp
        );

        // 2. Alinhar o INÍCIO do 'indicatorView' com o INÍCIO do 'selectedTextView'
        constraintSet.connect(
                indicatorView.getId(),
                ConstraintSet.START,
                selectedTextView.getId(),
                ConstraintSet.START
        );

        // 3. Alinhar o FIM do 'indicatorView' com o FIM do 'selectedTextView'
        constraintSet.connect(
                indicatorView.getId(),
                ConstraintSet.END,
                selectedTextView.getId(),
                ConstraintSet.END
        );

        // 4. Definir a LARGURA do 'indicatorView' para preencher o espaço entre suas constraints START e END
        constraintSet.constrainWidth(
                indicatorView.getId(),
                ConstraintSet.MATCH_CONSTRAINT
        );

        // Aplica a animação com transição suave
        constraintSet.applyTo(constraintLayout);
        
        // Adiciona uma animação de bounce no indicador para feedback visual
        AppAnimationUtils.animateBounce(indicatorView);
    }

    // Função utilitária para converter dp em pixels
    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        // Usar displayMetrics.density é geralmente mais simples e recomendado
        return Math.round(dp * displayMetrics.density);

    }
}
