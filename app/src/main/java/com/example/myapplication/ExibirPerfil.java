package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button; // Para o botão Editar Perfil
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import com.example.myapplication.api.ApiService;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.bitmap.Transform;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    private ConstraintLayout cardContentLayout;
    private ConstraintLayout profileScrollContainer;
    private NestedScrollView scrollContent;

    private LinearLayout btnVoltar;
    
    // Variáveis para postagens
    private RecyclerView recyclerViewPostagens;
    private PostagemAdapter postagemAdapter;
    private List<ModeloPostagem> listaPostagens;
    private String userIdAtual;
    private TextView exibirNome;
    private TextView exibirUser;
    private TextView textViewBio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exibir_perfil);

        apiService = new ApiService(this);

        constraintLayout = findViewById(R.id.main);
        cardContentLayout = findViewById(R.id.card_content);
        profileScrollContainer = findViewById(R.id.profile_scroll_container);
        scrollContent = findViewById(R.id.scroll_content);
        textViewPostagem = findViewById(R.id.textViewPostagem);
        textViewComentarios = findViewById(R.id.textViewComentarios);
        textViewSobreMim = findViewById(R.id.textViewSobreMim);
        indicatorView = findViewById(R.id.indicatorView);
        profileImageView = findViewById(R.id.imageView4);
        btnSalvar = findViewById(R.id.btnSalvar);
        exibirNome = findViewById(R.id.exibirNome);
        exibirUser = findViewById(R.id.exibirUser);
        textViewBio = findViewById(R.id.textViewBio);

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

        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> {
                // Aplica animação de clique
                AppAnimationUtils.animateButtonClick(btnVoltar, () -> {
                    Intent intent = new Intent(ExibirPerfil.this, ConfActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
            });
        } else {
            Log.w(TAG, "Botão 'btnVoltar' não encontrado!");
        }


        // Configura o estado inicial e listeners
        if (textViewPostagem != null) {
            // Aguarda o layout estar pronto antes de atualizar o indicador
            textViewPostagem.post(new Runnable() {
                @Override
                public void run() {
                    if (indicatorView != null) {
                        updateIndicator(textViewPostagem);
                        updateTabColors(textViewPostagem);
                    }
                }
            });
            textViewPostagem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aplica animação de bounce para feedback visual
                    AppAnimationUtils.animateBounce(textViewPostagem);
                    updateIndicator(textViewPostagem);
                    updateTabColors(textViewPostagem);
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
                    updateTabColors(textViewComentarios);
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
                    updateTabColors(textViewSobreMim);
                    // TODO: Lógica para mostrar conteúdo de Sobre Mim
                }
            });
        } else {
            Log.w(TAG, "TextView 'textViewSobreMim' não encontrado!");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Aplica padding apenas no topo para a sombra superior, sem padding no bottom
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Aplica animações de entrada para os elementos principais
        animateElementsOnStart();
        
        // Inicializar RecyclerView para postagens
        inicializarRecyclerViewPostagens();
        
        // Carregar postagens do usuário atual
        mostrarPostagensUsuario();

        // Carregar nome de exibição e usuário (arroba)
        loadUserProfileData();
    }
    
    private void inicializarRecyclerViewPostagens() {
        recyclerViewPostagens = findViewById(R.id.recycler_postagens_perfil);
        if (recyclerViewPostagens != null) {
            listaPostagens = new ArrayList<>();
            postagemAdapter = new PostagemAdapter(this, listaPostagens);
            
            recyclerViewPostagens.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewPostagens.setAdapter(postagemAdapter);
            
            // Desabilitar scroll do RecyclerView para permitir scroll unificado no NestedScrollView
            recyclerViewPostagens.setNestedScrollingEnabled(false);
            
            // Garantir que o RecyclerView seja transparente para mostrar o gradiente do container
            recyclerViewPostagens.setBackgroundColor(android.graphics.Color.TRANSPARENT);
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
                                
                                JSONArray jsonArray = null;
                                
                                // Tenta primeiro como objeto JSON (formato com chave "postagens")
                                try {
                                    JSONObject root = new JSONObject(result);
                                    
                                    // Verifica se a resposta foi bem-sucedida
                                    if (!root.optBoolean("success", true)) {
                                        String error = root.optString("error", "Erro desconhecido");
                                        Log.e(TAG, "Erro no backend: " + error);
                                        Toast.makeText(ExibirPerfil.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    
                                    // Extrai o array de postagens
                                    jsonArray = root.optJSONArray("postagens");
                                    if (jsonArray == null) {
                                        // Se não tem chave "postagens", tenta como array direto
                                        Log.d(TAG, "Campo 'postagens' não encontrado, tentando como array direto...");
                                        jsonArray = new JSONArray(result);
                                    }
                                } catch (JSONException jsonEx) {
                                    // Se falhar como objeto, tenta como array direto
                                    Log.d(TAG, "Não é objeto JSON, tentando como array direto...");
                                    try {
                                        jsonArray = new JSONArray(result);
                                    } catch (JSONException jsonEx2) {
                                        Log.e(TAG, "Erro ao processar JSON: " + jsonEx2.getMessage());
                                        Toast.makeText(ExibirPerfil.this, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                
                                if (jsonArray == null) {
                                    Log.e(TAG, "Não foi possível processar a resposta como JSON");
                                    return;
                                }
                                
                                Log.d(TAG, "JSON processado com sucesso. Número de postagens: " + jsonArray.length());
                                
                                List<ModeloPostagem> novasPostagens = new ArrayList<>();
                                
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject postagemJson = jsonArray.getJSONObject(i);
                                    String postagemIdUsuario = postagemJson.getString("id_usuario");
                                    
                                    // Filtrar apenas postagens do usuário atual
                                    if (userIdAtual != null && userIdAtual.equals(postagemIdUsuario)) {
                                        ModeloPostagem postagem = new ModeloPostagem(
                                            postagemJson.getString("id"),
                                            postagemJson.getString("titulo"),
                                            postagemJson.getString("texto"),
                                            postagemJson.getString("id_usuario"),
                                            postagemJson.optString("nome_usuario", null),
                                            postagemJson.optString("foto_usuario", null),
                                            postagemJson.getString("id_obra"),
                                            postagemJson.optString("titulo_obra", null),
                                            postagemJson.optString("poster_obra", null),
                                            postagemJson.optString("data_criacao", postagemJson.optString("data_post", ""))
                                        );
                                        novasPostagens.add(postagem);
                                    } else {
                                        Log.d(TAG, "Postagem filtrada - não pertence ao usuário atual. ID da postagem: " + postagemIdUsuario + ", ID do usuário atual: " + userIdAtual);
                                    }
                                }
                                
                                Log.d(TAG, "Verificando se lista e adapter estão disponíveis...");
                                Log.d(TAG, "Lista postagens é nula? " + (listaPostagens == null));
                                Log.d(TAG, "Adapter é nulo? " + (postagemAdapter == null));
                                
                                if (listaPostagens != null && postagemAdapter != null) {
                                    Log.d(TAG, "Atualizando lista e adapter...");
                                    listaPostagens.clear();
                                    listaPostagens.addAll(novasPostagens);
                                    postagemAdapter.notifyDataSetChanged();
                                    
                                    // Garantir que o background do gradiente seja mantido
                                    if (constraintLayout != null) {
                                        constraintLayout.setBackgroundResource(R.drawable.my_gradiant4);
                                    }
                                    if (scrollContent != null) {
                                        scrollContent.setBackgroundResource(R.drawable.my_gradiant4);
                                    }
                                    if (profileScrollContainer != null) {
                                        profileScrollContainer.setBackgroundResource(R.drawable.my_gradiant4);
                                    }
                                    if (recyclerViewPostagens != null) {
                                        recyclerViewPostagens.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                                    }
                                    
                                    Log.d(TAG, "Postagens do usuário carregadas: " + novasPostagens.size());
                                    Log.d(TAG, "Adapter notificado da mudança");
                                    
                                    if (novasPostagens.size() == 0) {
                                        Log.d(TAG, "Usuário não possui postagens ainda");
                                    }
                                } else {
                                    Log.e(TAG, "ERRO: Lista ou adapter são nulos!");
                                }
                                
                            } catch (Exception jsonEx) {
                                Log.e(TAG, "Erro ao processar JSON das postagens: " + jsonEx.getMessage());
                                jsonEx.printStackTrace();
                                Toast.makeText(ExibirPerfil.this, "Erro ao processar postagens", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "Nenhuma postagem encontrada para o usuário");
                            if (listaPostagens != null && postagemAdapter != null) {
                                listaPostagens.clear();
                                postagemAdapter.notifyDataSetChanged();
                            }
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
        if (indicatorView == null || selectedTextView == null || cardContentLayout == null) {
            Log.w(TAG, "updateIndicator: Uma ou mais Views são nulas! Não é possível atualizar o indicador.");
            return;
        }

        // Aguarda o layout estar medido antes de calcular posições
        selectedTextView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    // Como o textView está dentro de um LinearLayout, calculamos a posição relativa
                    View parent = (View) selectedTextView.getParent();
                    if (parent == null) {
                        Log.w(TAG, "updateIndicator: Parent do TextView é nulo!");
                        return;
                    }

                    // Calcula a posição do TextView dentro do LinearLayout
                    int left = selectedTextView.getLeft();
                    int width = selectedTextView.getWidth();

                    // Usa ConstraintSet para posicionar o indicador abaixo do menu_abas
                    // Em vez de clonar, vamos criar um novo ConstraintSet e apenas definir as constraints necessárias
                    ConstraintSet constraintSet = new ConstraintSet();
                    
                    // Primeiro, clona o layout atual (agora que todos os elementos têm IDs)
                    try {
                        constraintSet.clone(cardContentLayout);
                    } catch (Exception cloneEx) {
                        Log.e(TAG, "Erro ao clonar ConstraintLayout: " + cloneEx.getMessage());
                        // Se o clone falhar, tenta uma abordagem alternativa usando LayoutParams
                        android.view.ViewGroup.MarginLayoutParams params = 
                            (android.view.ViewGroup.MarginLayoutParams) indicatorView.getLayoutParams();
                        if (params != null) {
                            params.width = width;
                            params.leftMargin = left;
                            params.rightMargin = parent.getWidth() - left - width;
                            indicatorView.setLayoutParams(params);
                            indicatorView.setVisibility(View.VISIBLE);
                            AppAnimationUtils.animateBounce(indicatorView);
                            return;
                        }
                        return;
                    }

                    // Conecta o indicador ao menu_abas (LinearLayout) na parte inferior
                    View menuAbas = findViewById(R.id.menu_abas);
                    if (menuAbas != null) {
                        constraintSet.connect(
                                indicatorView.getId(),
                                ConstraintSet.TOP,
                                menuAbas.getId(),
                                ConstraintSet.BOTTOM,
                                0
                        );
                    }

                    // Define a largura e posição horizontal usando margens calculadas
                    constraintSet.constrainWidth(indicatorView.getId(), width);
                    constraintSet.setMargin(indicatorView.getId(), ConstraintSet.START, left);
                    constraintSet.setMargin(indicatorView.getId(), ConstraintSet.END, 
                            parent.getWidth() - left - width);

                    // Aplica as mudanças
                    constraintSet.applyTo(cardContentLayout);
                    
                    // Torna o indicador visível
                    indicatorView.setVisibility(View.VISIBLE);
                    
                    // Adiciona uma animação de bounce no indicador para feedback visual
                    AppAnimationUtils.animateBounce(indicatorView);
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao atualizar indicador: " + e.getMessage(), e);
                }
            }
        });
    }

    // Função utilitária para converter dp em pixels
    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        // Usar displayMetrics.density é geralmente mais simples e recomendado
        return Math.round(dp * displayMetrics.density);
    }

    /**
     * Atualiza as cores das abas quando uma é selecionada
     */
    private void updateTabColors(TextView selectedTab) {
        int selectedColor = ContextCompat.getColor(this, R.color.white);
        int unselectedColor = ContextCompat.getColor(this, R.color.linhas);
        
        if (textViewPostagem != null) {
            textViewPostagem.setTextColor(
                textViewPostagem == selectedTab ? selectedColor : unselectedColor
            );
        }
        if (textViewComentarios != null) {
            textViewComentarios.setTextColor(
                textViewComentarios == selectedTab ? selectedColor : unselectedColor
            );
        }
        if (textViewSobreMim != null) {
            textViewSobreMim.setTextColor(
                textViewSobreMim == selectedTab ? selectedColor : unselectedColor
            );
        }
    }

    private void loadUserProfileData() {
        String userId = getSharedPreferences("RepArte", MODE_PRIVATE).getString("user_id", null);

        if (userId == null || userId.isEmpty()) {
            Log.e("Perfil", "user_id não encontrado nas SharedPreferences");
            if (exibirNome != null) exibirNome.setText("Nome do Usuário");
            if (exibirUser != null) exibirUser.setText("@usuario");
            return;
        }

        // Buscar nome de exibição (receber_perfil.php)
        apiService.buscarPerfil(userId, (e, result) -> {
            if (e != null) {
                Log.e("Perfil", "Erro ao buscar perfil", e);
                return;
            }
            try {
                if (result != null) {
                    JSONObject json = new JSONObject(result);
                    boolean success = json.optBoolean("success", false);
                    if (success) {
                        String nome = json.optString("nome", "");
                        String descricao = json.optString("descricao", "");
                        if (exibirNome != null && nome != null && !nome.isEmpty()) {
                            exibirNome.setText(nome);
                        }
                        if (textViewBio != null) {
                            if (descricao != null && !descricao.isEmpty()) {
                                textViewBio.setText(descricao);
                            } else {
                                textViewBio.setText("Adicione uma descrição sobre você!");
                                textViewBio.setAlpha(0.5f);
                            }
                        }
                    } else {
                        Log.w("Perfil", "receber_perfil retornou sucesso=false: " + json.optString("message"));
                    }
                }
            } catch (Exception ex) {
                Log.e("Perfil", "Falha ao processar JSON de receber_perfil", ex);
            }
        });

        // Buscar usuário/arrob a (receber_usuario.php)
        apiService.buscarUsuarioPorId(userId, (e, result) -> {
            if (e != null) {
                Log.e("Perfil", "Erro ao buscar usuário", e);
                return;
            }
            try {
                if (result != null) {
                    String raw = result;
                    String trimmed = raw.trim();
                    // Se vier HTML/DOCTYPE ou qualquer ruído, tentar isolar o bloco JSON
                    int firstBrace = trimmed.indexOf('{');
                    int lastBrace = trimmed.lastIndexOf('}');
                    String jsonCandidate = (firstBrace >= 0 && lastBrace > firstBrace) ? trimmed.substring(firstBrace, lastBrace + 1) : trimmed;
                    try {
                        JSONObject json = new JSONObject(jsonCandidate);
                        boolean success = json.optBoolean("success", false);
                        if (success) {
                            String usuario = json.optString("usuario", "");
                            if (exibirUser != null && usuario != null && !usuario.isEmpty()) {
                                exibirUser.setText(usuario);
                            }
                        } else {
                            Log.w("Perfil", "receber_usuario retornou sucesso=false: " + json.optString("message"));
                        }
                    } catch (Exception jsEx) {
                        // Fallback: tentar extrair o campo usuario por regex simples
                        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"usuario\"\\s*:\\s*\"([^\"]+)\"").matcher(jsonCandidate);
                        if (m.find()) {
                            String usuario = m.group(1);
                            if (exibirUser != null && usuario != null && !usuario.isEmpty()) {
                                exibirUser.setText(usuario);
                            }
                            Log.w("Perfil", "JSON inválido; 'usuario' extraído via regex. Conteúdo bruto iniciado por: " + trimmed.substring(0, Math.min(80, trimmed.length())));
                        } else {
                            throw jsEx;
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e("Perfil", "Falha ao processar JSON de receber_usuario", ex);
            }
        });


    }



}
