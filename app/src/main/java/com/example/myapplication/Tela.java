package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.example.myapplication.api.ApiService;
import com.koushikdutta.ion.bitmap.Transform;
import com.koushikdutta.ion.Ion;
import com.example.myapplication.PostagemAdapter;
import com.example.myapplication.ModeloPostagem;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Tela extends AppCompatActivity implements PostagemAdapter.OnPostagemClickListener {

    private ApiService apiService;
    private RecyclerView recyclerViewPostagens;
    private PostagemAdapter postagemAdapter;
    private List<ModeloPostagem> listaPostagens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela1);

        apiService = new ApiService(this);

        //declarar
        ImageView perfil;
        ImageView lapis_img;
        EditText pesquisar;
        Button vermais;



        //id
        perfil = findViewById(R.id.perfil);
        pesquisar = findViewById(R.id.pesquisar);
        vermais = findViewById(R.id.vermais1);
        lapis_img = findViewById(R.id.lapis_img);

        // Anima os elementos da tela principal
        animateMainScreenElements(perfil, pesquisar);
        
        // Configura as interações dos trending topics
        setupTrendingTopics();
        
        // Configura as interações dos amigos
        setupFriendsList();
        
        // Inicializa o RecyclerView das postagens
        inicializarRecyclerView();

        //evento botao ver mais
        vermais.setOnClickListener(view -> {
            Intent intent = new Intent(Tela.this, Postagem.class);
            startActivity(intent);
        });

        //evento botao postagem
        lapis_img.setOnClickListener(view -> {
            Intent intent = new Intent(Tela.this, Tela_post.class);
            startActivity(intent);
        });

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

        String userId = getSharedPreferences("RepArte", MODE_PRIVATE).getString("user_id", null);

        if (userId != null) {
            String fotoUrl = apiService.getFotoPerfilUrl(userId);
            Log.d("Perfil", "URL da foto: " + fotoUrl);
            
            // Carregar todas as postagens para o feed principal
            carregarTodasPostagens();

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
    private void animateMainScreenElements(ImageView perfil, EditText pesquisar) {
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

        // Anima os trending topics com slide up
        View trendingTitle = findViewById(R.id.trending_title);
        if (trendingTitle != null) {
            trendingTitle.setAlpha(0f);
            trendingTitle.setTranslationY(30f);
            trendingTitle.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(600)
                    .start();
        }

        // Anima a lista de amigos com slide up
        View friendsTitle = findViewById(R.id.friends_title);
        if (friendsTitle != null) {
            friendsTitle.setAlpha(0f);
            friendsTitle.setTranslationY(30f);
            friendsTitle.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(800)
                    .start();
        }
    }

    /**
     * Configura as interações dos trending topics
     */
    private void setupTrendingTopics() {
        // Configura cada trending topic
        setupTrendingTopic(R.id.trending_action, "#Ação");
        setupTrendingTopic(R.id.trending_romance, "#Romance");
        setupTrendingTopic(R.id.trending_arte, "#ArteContemporânea");
        setupTrendingTopic(R.id.trending_livro, "#LivroDaSemana");
        setupTrendingTopic(R.id.trending_festival, "#FestivalArte");
    }

    /**
     * Configura um trending topic específico
     */
    private void setupTrendingTopic(int buttonId, String hashtag) {
        Button button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> {
                // Aplica animação de clique
                AppAnimationUtils.animateButtonClick(button, () -> {
                    // TODO: Implementar busca por hashtag quando a funcionalidade estiver pronta
                    Toast.makeText(Tela.this, "Funcionalidade em desenvolvimento!", Toast.LENGTH_SHORT).show();
                });
            });
        }
    }

    /**
     * Configura as interações da lista de amigos
     */
    private void setupFriendsList() {
        // Configura o link "Adicionar Amigo"
        TextView addFriendLink = findViewById(R.id.add_friend_link);
        if (addFriendLink != null) {
            addFriendLink.setOnClickListener(v -> {
                // Aplica animação de clique
                AppAnimationUtils.animateButtonClick(addFriendLink, () -> {
                    // Navega para a tela de adicionar amigos
                    Intent intent = new Intent(Tela.this, AdicionarAmigoActivity.class);
                    startActivity(intent);
                });
            });
        }

        // Configura cada amigo
        setupFriend(R.id.friend_1, "Ana");
        setupFriend(R.id.friend_2, "Carlos");
        setupFriend(R.id.friend_3, "Maria");
        setupFriend(R.id.friend_4, "João");
        setupFriend(R.id.friend_5, "Sofia");
    }

    /**
     * Configura um amigo específico
     */
    private void setupFriend(int friendLayoutId, String friendName) {
        View friendLayout = findViewById(friendLayoutId);
        if (friendLayout != null) {
            friendLayout.setOnClickListener(v -> {
                // Aplica animação leve de toque
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.avatar_tap);
                friendLayout.startAnimation(animation);
                
                // TODO: Implementar navegação para perfil do amigo quando a funcionalidade estiver pronta
                // Por enquanto, não faz nada ao clicar
            });
        }
    }
    
    private void inicializarRecyclerView() {
        recyclerViewPostagens = findViewById(R.id.recycler_postagens);
        listaPostagens = new ArrayList<>();
        
        postagemAdapter = new PostagemAdapter(this, listaPostagens);
        postagemAdapter.setOnPostagemClickListener(this);
        
        recyclerViewPostagens.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPostagens.setAdapter(postagemAdapter);
    }
    
    private void carregarTodasPostagens() {
        apiService.buscarTodasPostagens(new com.koushikdutta.async.future.FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                runOnUiThread(() -> {
                    if (e != null) {
                        Log.e("Tela", "Erro ao carregar postagens: " + e.getMessage());
                        Toast.makeText(Tela.this, "Erro ao carregar postagens", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (result != null && !result.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(result);
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
                            
                            listaPostagens.clear();
                            listaPostagens.addAll(novasPostagens);
                            postagemAdapter.notifyDataSetChanged();
                            
                            Log.d("Tela", "Postagens carregadas: " + novasPostagens.size());
                            
                        } catch (Exception jsonEx) {
                            Log.e("Tela", "Erro ao processar JSON das postagens: " + jsonEx.getMessage());
                        }
                    } else {
                        Log.d("Tela", "Nenhuma postagem encontrada");
                    }
                });
            }
        });
    }
    
    private void carregarPostagensUsuario(String userId) {
        apiService.buscarPostagensUsuario(userId, new com.koushikdutta.async.future.FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                runOnUiThread(() -> {
                    if (e != null) {
                        Log.e("Tela", "Erro ao carregar postagens: " + e.getMessage());
                        Toast.makeText(Tela.this, "Erro ao carregar postagens", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (result != null && !result.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(result);
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
                            
                            listaPostagens.clear();
                            listaPostagens.addAll(novasPostagens);
                            postagemAdapter.notifyDataSetChanged();
                            
                            Log.d("Tela", "Postagens carregadas: " + novasPostagens.size());
                            
                        } catch (Exception jsonEx) {
                            Log.e("Tela", "Erro ao processar JSON das postagens: " + jsonEx.getMessage());
                        }
                    } else {
                        Log.d("Tela", "Nenhuma postagem encontrada");
                    }
                });
            }
        });
    }
    
    // Implementação dos métodos da interface OnPostagemClickListener
    @Override
    public void onPostagemClick(ModeloPostagem postagem) {
        // TODO: Abrir detalhes da postagem
        Toast.makeText(this, "Postagem: " + postagem.getTitulo(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onUsuarioClick(String userId) {
        // TODO: Abrir perfil do usuário
        Toast.makeText(this, "Abrindo perfil do usuário: " + userId, Toast.LENGTH_SHORT).show();
    }
    
    @Override
   public void onObraClick(String obraId) {
        // TODO: Abrir detalhes da obra
        Toast.makeText(this, "Abrindo detalhes da obra: " + obraId, Toast.LENGTH_SHORT).show();
    }
}



