package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
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
import androidx.core.content.ContextCompat;
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
        ImageView home_img;
        ImageView lapis_img;
        ImageView sino_img;
        EditText pesquisar;
        Button vermais;



        //id
        perfil = findViewById(R.id.perfil);
        pesquisar = findViewById(R.id.pesquisar);
        // vermais = findViewById(R.id.vermais1);
        home_img = findViewById(R.id.home_img);
        lapis_img = findViewById(R.id.lapis_img);
        sino_img = findViewById(R.id.sino_txt);
        
        // Debug: verificar se o botão foi encontrado
      //  if (vermais == null) {
        //    Log.e("Tela", "ERRO: Botão vermais1 não foi encontrado!");
      //      Toast.makeText(this, "Erro: Botão não encontrado", Toast.LENGTH_LONG).show();
      //  } else {
      //      Log.d("Tela", "Botão vermais1 encontrado com sucesso!");
     //       Log.d("Tela", "Botão é clicável: " + vermais.isClickable());
      //      Log.d("Tela", "Botão é focado: " + vermais.isFocusable());
     //   }

        // Anima os elementos da tela principal
        animateMainScreenElements(perfil, pesquisar);
        
        // Configura as interações dos trending topics
        setupTrendingTopics();
        
        // Configura as interações dos amigos
        setupFriendsList();
        
        // Inicializa o RecyclerView das postagens
        inicializarRecyclerView();

        //evento botao ver mais
      //  if (vermais != null) {
        //    vermais.setOnClickListener(view -> {
         //       Intent intent = new Intent(Tela.this, Postagem.class);
        //        startActivity(intent);
          //  });
    //    }

        // Seleção inicial: Home ativo (quadrado laranja, ícone branco)
        updateBottomMenuSelection(R.id.home_img);

        // evento botao home
        if (home_img != null) {
            home_img.setOnClickListener(view -> {
                updateBottomMenuSelection(R.id.home_img);
                // Scroll para o topo da tela
                androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.scroll_content);
                if (scrollView != null) {
                    scrollView.smoothScrollTo(0, 0);
                    recyclerViewPostagens.smoothScrollToPosition(0);
                }
            });
        }

        // evento botao postagem (lápis)
        if (lapis_img != null) {
            lapis_img.setOnClickListener(view -> {
                updateBottomMenuSelection(R.id.lapis_img);
                Intent intent = new Intent(Tela.this, Tela_post.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_slide_in_right, R.anim.fade_slide_out_left);
            });
        }

        // evento botao sino (notificações)
        if (sino_img != null) {
            sino_img.setOnClickListener(view -> {
                updateBottomMenuSelection(R.id.sino_txt);
                Intent intent = new Intent(Tela.this, NotificacoesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_slide_in_right, R.anim.fade_slide_out_left);
            });
        }

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

            // Carregar foto no perfil principal
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
            
            // Carregar foto no menu compacto também
            ImageView perfilCompacto = findViewById(R.id.perfil_compacto);
            if (perfilCompacto != null) {
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
                                return "circleTransformProfileCompact";
                            }
                        })
                        .intoImageView(perfilCompacto);
            }
        } else {
            Log.e("Perfil", "User ID é nulo, não foi possível carregar a foto.");
            perfil.setImageResource(R.drawable.user_white);
            ImageView perfilCompacto = findViewById(R.id.perfil_compacto);
            if (perfilCompacto != null) {
                perfilCompacto.setImageResource(R.drawable.user_white);
            }
        }
    }

    /**
     * Atualiza as cores dos ícones do menu inferior para indicar a aba selecionada.
     */
    private void updateBottomMenuSelection(int selectedViewId) {
        ImageView home = findViewById(R.id.home_img);
        ImageView lapis = findViewById(R.id.lapis_img);
        ImageView sino = findViewById(R.id.sino_txt);

        View homeContainer = findViewById(R.id.home_container);
        View lapisContainer = findViewById(R.id.lapis_container);
        View sinoContainer = findViewById(R.id.sino_container);

        int white = ContextCompat.getColor(this, android.R.color.white);

        // Atualiza fundos
        if (homeContainer != null) {
            homeContainer.setBackground(ContextCompat.getDrawable(this, selectedViewId == R.id.home_img ? R.drawable.div_tela3 : R.drawable.div_tela2));
        }
        if (lapisContainer != null) {
            lapisContainer.setBackground(ContextCompat.getDrawable(this, selectedViewId == R.id.lapis_img ? R.drawable.div_tela3 : R.drawable.div_tela2));
        }
        if (sinoContainer != null) {
            sinoContainer.setBackground(ContextCompat.getDrawable(this, selectedViewId == R.id.sino_txt ? R.drawable.div_tela3 : R.drawable.div_tela2));
        }

        // Ícones permanecem brancos
        if (home != null) home.setColorFilter(white, PorterDuff.Mode.SRC_IN);
        if (lapis != null) lapis.setColorFilter(white, PorterDuff.Mode.SRC_IN);
        if (sino != null) sino.setColorFilter(white, PorterDuff.Mode.SRC_IN);
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
        
        // Desabilitar scroll do RecyclerView para permitir scroll unificado no NestedScrollView
        recyclerViewPostagens.setNestedScrollingEnabled(false);
        
        // Configurar scroll listener para controlar visibilidade do menu compacto
        configurarScrollListener();
    }
    
    private void configurarScrollListener() {
        androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.scroll_content);
        if (scrollView != null) {
            View menuCompacto = findViewById(R.id.menu_compacto);
            View menuSuperior = findViewById(R.id.menu_superior);
            View trendingTitle = findViewById(R.id.trending_title);
            View friendsTitle = findViewById(R.id.friends_title);
            EditText pesquisarCompacto = findViewById(R.id.pesquisar_compacto);
            ImageView perfilCompacto = findViewById(R.id.perfil_compacto);
            EditText pesquisar = findViewById(R.id.pesquisar);
            
            scrollView.setOnScrollChangeListener(new androidx.core.widget.NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(androidx.core.widget.NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    // Calcular altura do menu superior completo
                    int alturaMenuSuperior = menuSuperior != null ? menuSuperior.getHeight() : 0;
                    EditText pesquisarView = findViewById(R.id.pesquisar);
                    int alturaPesquisar = pesquisarView != null ? pesquisarView.getHeight() : 0;
                    int alturaTrending = trendingTitle != null ? trendingTitle.getTop() : 0;
                    int threshold = alturaMenuSuperior + alturaPesquisar + 100; // Threshold para mostrar menu compacto
                    
                    // Mostrar menu compacto quando scrollar para baixo além do threshold
                    if (scrollY > threshold && menuCompacto != null) {
                        if (menuCompacto.getVisibility() != View.VISIBLE) {
                            menuCompacto.setVisibility(View.VISIBLE);
                            menuCompacto.animate().alpha(1f).setDuration(200).start();
                        }
                    } else if (scrollY <= threshold && menuCompacto != null) {
                        if (menuCompacto.getVisibility() == View.VISIBLE) {
                            menuCompacto.animate().alpha(0f).setDuration(200).withEndAction(() -> {
                                menuCompacto.setVisibility(View.GONE);
                            }).start();
                        }
                    }
                }
            });
            
            // Configurar eventos do menu compacto
            if (pesquisarCompacto != null) {
                pesquisarCompacto.setOnClickListener(v -> {
                    Intent intent = new Intent(Tela.this, BuscaActivity.class);
                    startActivity(intent);
                });
            }
            
            if (perfilCompacto != null) {
                perfilCompacto.setOnClickListener(v -> {
                    Intent intent = new Intent(Tela.this, ConfActivity.class);
                    startActivity(intent);
                });
            }
        }
    }
    
    private void carregarTodasPostagens() {
        Log.d("Tela", "=== INICIANDO CARREGAMENTO DE POSTAGENS ===");
        apiService.buscarTodasPostagens(new com.koushikdutta.async.future.FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                Log.d("Tela", "=== CALLBACK RECEBIDO NO CARREGAMENTO ===");
                Log.d("Tela", "Exception: " + (e != null ? e.getMessage() : "null"));
                Log.d("Tela", "Result: " + (result != null ? "não nulo (tamanho: " + result.length() + ")" : "null"));
                
                // Criar variável final para usar na lambda
                final String finalResult;
                if (result == null || result.isEmpty()) {
                    if (result == null) {
                        Log.e("Tela", "Result é NULL!");
                    } else {
                        Log.w("Tela", "Resposta vazia do servidor");
                        Log.w("Tela", "Isso indica um problema no servidor PHP - ele deveria retornar JSON válido");
                        Log.w("Tela", "Aplicando fallback: retornando JSON vazio válido");
                        Log.w("Tela", "IMPORTANTE: Corrija o arquivo buscar_todas_postagens.php no servidor!");
                    }
                    // Fallback: criar JSON vazio válido quando servidor não responde corretamente
                    finalResult = "{\"success\":true,\"postagens\":[],\"total\":0}";
                } else {
                    finalResult = result;
                }
                
                runOnUiThread(() -> {
                    if (e != null) {
                        Log.e("Tela", "Erro ao carregar postagens: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(Tela.this, "Erro ao carregar postagens: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    
                    if (finalResult == null || finalResult.isEmpty()) {
                        Log.e("Tela", "Result é NULL ou vazio após fallback!");
                        Toast.makeText(Tela.this, "Resposta nula do servidor", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (finalResult.equals("{\"success\":true,\"postagens\":[],\"total\":0}") && (result == null || result.isEmpty())) {
                        Toast.makeText(Tela.this, "Servidor retornou resposta vazia. Verifique o PHP.", Toast.LENGTH_LONG).show();
                    }
                    
                    Log.d("Tela", "Processando resposta JSON...");
                    Log.d("Tela", "Resposta completa: " + finalResult);
                    
                    try {
                        // O backend retorna um objeto JSON com a chave "postagens"
                        JSONObject root = new JSONObject(finalResult);
                        Log.d("Tela", "JSON root parseado com sucesso");
                        
                        // Verificar se a resposta foi bem-sucedida
                        boolean success = root.optBoolean("success", false);
                        Log.d("Tela", "Success: " + success);
                        
                        if (!success) {
                            String error = root.optString("error", "Erro desconhecido");
                            Log.e("Tela", "Erro no backend: " + error);
                            Toast.makeText(Tela.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        // Extrair o array de postagens
                        JSONArray jsonArray = root.optJSONArray("postagens");
                        if (jsonArray == null) {
                            Log.e("Tela", "Campo 'postagens' não encontrado na resposta");
                            // Log das chaves disponíveis
                            java.util.Iterator<String> keys = root.keys();
                            StringBuilder chaves = new StringBuilder();
                            while (keys.hasNext()) {
                                chaves.append(keys.next()).append(", ");
                            }
                            Log.e("Tela", "Chaves disponíveis: " + chaves.toString());
                            Toast.makeText(Tela.this, "Formato de resposta inválido", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        Log.d("Tela", "Array de postagens encontrado com " + jsonArray.length() + " itens");
                        List<ModeloPostagem> novasPostagens = new ArrayList<>();
                        
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject postagemJson = jsonArray.getJSONObject(i);
                            // Criar postagem apenas com os dados que vêm do PHP (conforme criar_postagens.php)
                            ModeloPostagem postagem = new ModeloPostagem(
                                postagemJson.getString("id"),
                                postagemJson.getString("titulo"),
                                postagemJson.getString("texto"),
                                postagemJson.getString("id_usuario"),
                                null, // nome_usuario - não vem mais do PHP, será buscado separadamente se necessário
                                null, // foto_usuario - não vem mais do PHP, será buscado separadamente se necessário
                                postagemJson.getString("id_obra"),
                                null, // titulo_obra - não vem mais do PHP, será buscado separadamente se necessário
                                null, // poster_obra - não vem mais do PHP, será buscado separadamente se necessário
                                postagemJson.optString("data_post", postagemJson.optString("data_criacao", "")) // data_post ou data_criacao
                            );
                            novasPostagens.add(postagem);
                        }
                        
                        listaPostagens.clear();
                        listaPostagens.addAll(novasPostagens);
                        postagemAdapter.notifyDataSetChanged();
                        
                        Log.d("Tela", "Postagens carregadas com sucesso: " + novasPostagens.size());
                        if (novasPostagens.size() > 0) {
                            Toast.makeText(Tela.this, "Carregadas " + novasPostagens.size() + " postagens", Toast.LENGTH_SHORT).show();
                        }
                        
                    } catch (Exception jsonEx) {
                        Log.e("Tela", "Erro ao processar JSON das postagens: " + jsonEx.getMessage());
                        Log.e("Tela", "Resposta recebida: " + finalResult);
                        jsonEx.printStackTrace();
                        Toast.makeText(Tela.this, "Erro ao processar resposta: " + jsonEx.getMessage(), Toast.LENGTH_LONG).show();
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
                            // O backend retorna um objeto JSON com a chave "postagens"
                            JSONObject root = new JSONObject(result);
                            
                            // Verificar se a resposta foi bem-sucedida
                            if (!root.optBoolean("success", false)) {
                                String error = root.optString("error", "Erro desconhecido");
                                Log.e("Tela", "Erro no backend: " + error);
                                Toast.makeText(Tela.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            
                            // Extrair o array de postagens
                            JSONArray jsonArray = root.optJSONArray("postagens");
                            if (jsonArray == null) {
                                Log.e("Tela", "Campo 'postagens' não encontrado na resposta");
                                return;
                            }
                            
                            List<ModeloPostagem> novasPostagens = new ArrayList<>();
                            
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject postagemJson = jsonArray.getJSONObject(i);
                                // Criar postagem apenas com os dados que vêm do PHP (conforme criar_postagens.php)
                                ModeloPostagem postagem = new ModeloPostagem(
                                    postagemJson.getString("id"),
                                    postagemJson.getString("titulo"),
                                    postagemJson.getString("texto"),
                                    postagemJson.getString("id_usuario"),
                                    null, // nome_usuario - não vem mais do PHP, será buscado separadamente se necessário
                                    null, // foto_usuario - não vem mais do PHP, será buscado separadamente se necessário
                                    postagemJson.getString("id_obra"),
                                    null, // titulo_obra - não vem mais do PHP, será buscado separadamente se necessário
                                    null, // poster_obra - não vem mais do PHP, será buscado separadamente se necessário
                                    postagemJson.optString("data_post", postagemJson.optString("data_criacao", "")) // data_post ou data_criacao
                                );
                                novasPostagens.add(postagem);
                            }
                            
                            listaPostagens.clear();
                            listaPostagens.addAll(novasPostagens);
                            postagemAdapter.notifyDataSetChanged();
                            
                            Log.d("Tela", "Postagens carregadas: " + novasPostagens.size());
                            
                        } catch (Exception jsonEx) {
                            Log.e("Tela", "Erro ao processar JSON das postagens: " + jsonEx.getMessage());
                            Log.e("Tela", "Resposta recebida: " + result);
                            jsonEx.printStackTrace();
                        }
                    } else {
                        Log.d("Tela", "Resposta vazia do servidor");
                    }
                });
            }
        });
    }
    
    // Implementação dos métodos da interface OnPostagemClickListener
    @Override
    public void onPostagemClick(ModeloPostagem postagem) {
        // Abrir tela de postagem completa com os dados
        Intent intent = new Intent(Tela.this, Postagem.class);
        intent.putExtra("postagem_id", postagem.getId());
        intent.putExtra("postagem_titulo", postagem.getTitulo());
        intent.putExtra("postagem_texto", postagem.getTexto());
        intent.putExtra("postagem_id_usuario", postagem.getIdUsuario());
        intent.putExtra("postagem_nome_usuario", postagem.getNomeUsuario());
        intent.putExtra("postagem_id_obra", postagem.getIdObra());
        intent.putExtra("postagem_titulo_obra", postagem.getTituloObra());
        intent.putExtra("postagem_poster_obra", postagem.getPosterObra());
        intent.putExtra("postagem_data_criacao", postagem.getDataCriacao());
        intent.putExtra("postagem_curtidas", postagem.getCurtidas());
        intent.putExtra("postagem_comentarios", postagem.getComentarios());
        startActivity(intent);
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



