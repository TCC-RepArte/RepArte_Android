package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.api.TMDBManager;
import com.example.myapplication.api.GoogleBooksManager;
import com.example.myapplication.api.MetManager;
import java.util.ArrayList;
import java.util.List;

public class SelecaoObraActivity extends AppCompatActivity implements AdapterSelecaoObra.OnItemClickListener {
    
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView mensagemErro;
    private AdapterSelecaoObra adapter;
    private List<ModeloFilme> listaObras;
    
    // Filtros
    private TextView filtroTodos, filtroFilmes, filtroSeries, filtroLivros, filtroArtes;
    private String filtroAtivo = "todos";
    
    // API Managers
    private TMDBManager tmdbManager;
    private GoogleBooksManager googleBooksManager;
    private MetManager metManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_obra);

        // Inicializar views
        inicializarViews();
        
        // Inicializar API Managers
        tmdbManager = new TMDBManager();
        googleBooksManager = new GoogleBooksManager();
        metManager = MetManager.getInstance();
        
        // Configurar RecyclerView
        configurarRecyclerView();
        
        // Configurar SearchView
        configurarSearchView();
        
        // Configurar botão voltar
        configurarBotaoVoltar();
        
        // Configurar filtros
        configurarFiltros();
        
        // Inicializar "Todos" como selecionado
        inicializarFiltroPadrao();
        
        // Carregar obras populares por padrão
        carregarObrasPopulares();
    }

    private void inicializarViews() {
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_resultados);
        progressBar = findViewById(R.id.progress_bar);
        mensagemErro = findViewById(R.id.mensagem_erro);
        
        // Filtros
        filtroTodos = findViewById(R.id.filtro_todos);
        filtroFilmes = findViewById(R.id.filtro_filmes);
        filtroSeries = findViewById(R.id.filtro_series);
        filtroLivros = findViewById(R.id.filtro_livros);
        filtroArtes = findViewById(R.id.filtro_artes);
    }

    private void configurarRecyclerView() {
        listaObras = new ArrayList<>();
        adapter = new AdapterSelecaoObra(this, listaObras);
        adapter.setOnItemClickListener(this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void configurarSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    realizarBusca(query.trim());
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Busca em tempo real quando o usuário digita
                if (newText != null && newText.length() >= 3) {
                    realizarBusca(newText.trim());
                } else if (newText == null || newText.isEmpty()) {
                    // Se a busca estiver vazia, mostrar obras populares
                    carregarObrasPopulares();
                }
                return true;
            }
        });
    }

    private void configurarBotaoVoltar() {
        findViewById(R.id.btn_voltar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void configurarFiltros() {
        filtroTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarFiltro("todos", filtroTodos);
            }
        });

        filtroFilmes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarFiltro("filmes", filtroFilmes);
            }
        });

        filtroSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarFiltro("series", filtroSeries);
            }
        });

        filtroLivros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarFiltro("livros", filtroLivros);
            }
        });

        filtroArtes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarFiltro("artes", filtroArtes);
            }
        });
    }

    private void selecionarFiltro(String filtro, TextView filtroSelecionado) {
        // Resetar todos os filtros
        filtroTodos.setBackgroundTintList(null);
        filtroFilmes.setBackgroundTintList(null);
        filtroSeries.setBackgroundTintList(null);
        filtroLivros.setBackgroundTintList(null);
        filtroArtes.setBackgroundTintList(null);

        // Selecionar o filtro clicado
        filtroSelecionado.setBackgroundTintList(getResources().getColorStateList(R.color.amarelo_filtro));
        filtroAtivo = filtro;

        // Aplicar filtro se houver termo de busca
        String query = searchView.getQuery().toString();
        if (query != null && !query.trim().isEmpty()) {
            realizarBusca(query.trim());
        } else {
            carregarObrasPopulares();
        }
    }

    private void inicializarFiltroPadrao() {
        filtroTodos.setBackgroundTintList(getResources().getColorStateList(R.color.amarelo_filtro));
        filtroAtivo = "todos";
    }

    private void carregarObrasPopulares() {
        mostrarLoading(true);
        
        // Contadores para controlar quantas buscas foram completadas
        final int[] buscaCompletas = {0};
        final int totalBuscas = 4; // filmes, séries, livros, artes
        final List<ModeloFilme> todasObras = new ArrayList<>();
        
        // Callback para combinar resultados
        Runnable combinarResultados = () -> {
            runOnUiThread(() -> {
                mostrarLoading(false);
                if (todasObras.isEmpty()) {
                    mostrarMensagemErro(true);
                    mensagemErro.setText("Nenhuma obra popular encontrada");
                } else {
                    exibirResultados(todasObras);
                }
            });
        };
        
        // Carregar filmes populares
        tmdbManager.getPopularMovies(new TMDBManager.PopularCallback() {
            @Override
            public void onSuccess(List<ModeloFilme> filmes) {
                synchronized (todasObras) {
                    todasObras.addAll(filmes);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
            
            @Override
            public void onError(String error) {
                synchronized (todasObras) {
                    android.util.Log.e("SelecaoObraActivity", "Erro ao carregar filmes populares: " + error);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
        });
        
        // Carregar séries populares
        tmdbManager.getPopularTVShows(new TMDBManager.PopularCallback() {
            @Override
            public void onSuccess(List<ModeloFilme> series) {
                synchronized (todasObras) {
                    todasObras.addAll(series);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
            
            @Override
            public void onError(String error) {
                synchronized (todasObras) {
                    android.util.Log.e("SelecaoObraActivity", "Erro ao carregar séries populares: " + error);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
        });
        
        // Carregar livros populares
        googleBooksManager.getPopularBooks(new GoogleBooksManager.PopularCallback() {
            @Override
            public void onSuccess(List<ModeloFilme> livros) {
                synchronized (todasObras) {
                    todasObras.addAll(livros);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
            
            @Override
            public void onError(String error) {
                synchronized (todasObras) {
                    android.util.Log.e("SelecaoObraActivity", "Erro ao carregar livros populares: " + error);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
        });
        
        // Carregar obras de arte em destaque
        metManager.getHighlightedArtworks(new MetManager.MetCallback<List<com.example.myapplication.model.MetArtwork>>() {
            @Override
            public void onSuccess(List<com.example.myapplication.model.MetArtwork> artworks) {
                synchronized (todasObras) {
                    // Converter MetArtwork para ModeloFilme
                    for (com.example.myapplication.model.MetArtwork artwork : artworks) {
                        ModeloFilme modelo = new ModeloFilme(
                            artwork.getObjectID(),
                            artwork.getTitle(),
                            artwork.getPrimaryImage(),
                            artwork.getObjectDate(),
                            artwork.getRating(),
                            "artwork"
                        );
                        modelo.setVotos(artwork.getRatingCount());
                        modelo.setOriginalId(String.valueOf(artwork.getObjectID()));
                        todasObras.add(modelo);
                    }
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
            
            @Override
            public void onError(String error) {
                synchronized (todasObras) {
                    android.util.Log.e("SelecaoObraActivity", "Erro ao carregar obras de arte populares: " + error);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
        });
    }

    private void realizarBusca(String termo) {
        // Validar termo de busca
        if (termo == null || termo.trim().isEmpty()) {
            mostrarLoading(false);
            mostrarMensagemErro(true);
            mensagemErro.setText("Digite um termo para buscar");
            return;
        }
        
        String termoLimpo = termo.trim();
        if (termoLimpo.length() < 2) {
            mostrarLoading(false);
            mostrarMensagemErro(true);
            mensagemErro.setText("Digite pelo menos 2 caracteres para buscar");
            return;
        }
        
        mostrarLoading(true);
        esconderMensagemErro();
        
        switch (filtroAtivo) {
            case "filmes":
                tmdbManager.searchMovies(termo, new TMDBManager.SearchCallback() {
                    @Override
                    public void onSuccess(List<ModeloFilme> filmes) {
                        runOnUiThread(() -> {
                            mostrarLoading(false);
                            if (filmes.isEmpty()) {
                                mostrarMensagemErro(true);
                                mensagemErro.setText("Nenhum filme encontrado para: " + termo);
                            } else {
                                exibirResultados(filmes);
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            mostrarLoading(false);
                            mostrarMensagemErro(true);
                            mensagemErro.setText("Erro na busca: " + error);
                        });
                    }
                });
                break;
                
            case "series":
                tmdbManager.searchTVShows(termo, new TMDBManager.SearchCallback() {
                    @Override
                    public void onSuccess(List<ModeloFilme> series) {
                        runOnUiThread(() -> {
                            mostrarLoading(false);
                            if (series.isEmpty()) {
                                mostrarMensagemErro(true);
                                mensagemErro.setText("Nenhuma série encontrada para: " + termo);
                            } else {
                                exibirResultados(series);
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            mostrarLoading(false);
                            mostrarMensagemErro(true);
                            mensagemErro.setText("Erro na busca: " + error);
                        });
                    }
                });
                break;
                
            case "livros":
                // Buscar apenas livros
                googleBooksManager.searchBooks(termo, new GoogleBooksManager.SearchCallback() {
                    @Override
                    public void onSuccess(List<ModeloFilme> livros) {
                        runOnUiThread(() -> {
                            mostrarLoading(false);
                            if (livros.isEmpty()) {
                                mostrarMensagemErro(true);
                                mensagemErro.setText("Nenhum livro encontrado para: " + termo);
                            } else {
                                exibirResultados(livros);
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            mostrarLoading(false);
                            mostrarMensagemErro(true);
                            mensagemErro.setText("Erro na busca de livros: " + error);
                        });
                    }
                });
                break;
                
            case "artes":
                // Buscar apenas obras de arte
                metManager.searchArtworks(termo, new MetManager.MetCallback<List<com.example.myapplication.model.MetArtwork>>() {
                    @Override
                    public void onSuccess(List<com.example.myapplication.model.MetArtwork> artworks) {
                        runOnUiThread(() -> {
                            mostrarLoading(false);
                            if (artworks.isEmpty()) {
                                mostrarMensagemErro(true);
                                mensagemErro.setText("Nenhuma obra de arte encontrada para: " + termo);
                            } else {
                                // Converter MetArtwork para ModeloFilme
                                List<ModeloFilme> obrasConvertidas = new ArrayList<>();
                                for (com.example.myapplication.model.MetArtwork artwork : artworks) {
                                    ModeloFilme modelo = new ModeloFilme(
                                        artwork.getObjectID(),
                                        artwork.getTitle(),
                                        artwork.getPrimaryImage(),
                                        artwork.getObjectDate(),
                                        artwork.getRating(),
                                        "artwork"
                                    );
                                    modelo.setVotos(artwork.getRatingCount());
                                    modelo.setOriginalId(String.valueOf(artwork.getObjectID()));
                                    obrasConvertidas.add(modelo);
                                }
                                exibirResultados(obrasConvertidas);
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            mostrarLoading(false);
                            mostrarMensagemErro(true);
                            // Mensagens mais amigáveis baseadas no tipo de erro
                            String mensagemErroFinal;
                            if (error != null) {
                                if (error.contains("Acesso negado") || error.contains("negado")) {
                                    mensagemErroFinal = "Acesso temporariamente bloqueado. Aguarde alguns instantes e tente novamente.";
                                } else if (error.contains("Muitas requisições")) {
                                    mensagemErroFinal = "Muitas requisições. Aguarde alguns instantes e tente novamente.";
                                } else if (error.contains("indisponível") || error.contains("servidor")) {
                                    mensagemErroFinal = "Serviço temporariamente indisponível. Tente novamente mais tarde.";
                                } else if (error.contains("conexão") || error.contains("internet")) {
                                    mensagemErroFinal = "Erro de conexão. Verifique sua internet e tente novamente.";
                                } else if (error.contains("Tempo de conexão")) {
                                    mensagemErroFinal = "Tempo de conexão esgotado. Tente novamente.";
                                } else {
                                    mensagemErroFinal = "Erro ao buscar obras de arte: " + error;
                                }
                            } else {
                                mensagemErroFinal = "Erro desconhecido ao buscar obras de arte";
                            }
                            mensagemErro.setText(mensagemErroFinal);
                        });
                    }
                });
                break;
                
            default: // "todos"
                // Buscar filmes, séries e livros
                buscarTodosOsResultados(termo);
                break;
        }
    }
    
    private void buscarTodosOsResultados(String termo) {
        // Contadores para controlar quantas buscas foram completadas
        final int[] buscaCompletas = {0};
        final int totalBuscas = 4; // filmes, séries, livros, artes
        final List<ModeloFilme> todosResultados = new ArrayList<>();
        
        // Callback para combinar resultados
        Runnable combinarResultados = () -> {
            runOnUiThread(() -> {
                mostrarLoading(false);
                if (todosResultados.isEmpty()) {
                    mostrarMensagemErro(true);
                    mensagemErro.setText("Nenhum resultado encontrado para: " + termo);
                } else {
                    exibirResultados(todosResultados);
                }
            });
        };
        
        // Buscar filmes
        tmdbManager.searchMovies(termo, new TMDBManager.SearchCallback() {
            @Override
            public void onSuccess(List<ModeloFilme> filmes) {
                synchronized (todosResultados) {
                    todosResultados.addAll(filmes);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
            
            @Override
            public void onError(String error) {
                synchronized (todosResultados) {
                    android.util.Log.e("SelecaoObraActivity", "Erro ao buscar filmes: " + error);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
        });
        
        // Buscar séries
        tmdbManager.searchTVShows(termo, new TMDBManager.SearchCallback() {
            @Override
            public void onSuccess(List<ModeloFilme> series) {
                synchronized (todosResultados) {
                    todosResultados.addAll(series);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
            
            @Override
            public void onError(String error) {
                synchronized (todosResultados) {
                    android.util.Log.e("SelecaoObraActivity", "Erro ao buscar séries: " + error);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
        });
        
        // Buscar livros
        googleBooksManager.searchBooks(termo, new GoogleBooksManager.SearchCallback() {
            @Override
            public void onSuccess(List<ModeloFilme> livros) {
                synchronized (todosResultados) {
                    todosResultados.addAll(livros);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
            
            @Override
            public void onError(String error) {
                synchronized (todosResultados) {
                    android.util.Log.e("SelecaoObraActivity", "Erro ao buscar livros: " + error);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
        });
        
        // Buscar obras de arte
        metManager.searchArtworks(termo, new MetManager.MetCallback<List<com.example.myapplication.model.MetArtwork>>() {
            @Override
            public void onSuccess(List<com.example.myapplication.model.MetArtwork> artworks) {
                synchronized (todosResultados) {
                    // Converter MetArtwork para ModeloFilme
                    for (com.example.myapplication.model.MetArtwork artwork : artworks) {
                        ModeloFilme modelo = new ModeloFilme(
                            artwork.getObjectID(),
                            artwork.getTitle(),
                            artwork.getPrimaryImage(),
                            artwork.getObjectDate(),
                            artwork.getRating(),
                            "artwork"
                        );
                        modelo.setVotos(artwork.getRatingCount());
                        modelo.setOriginalId(String.valueOf(artwork.getObjectID()));
                        todosResultados.add(modelo);
                    }
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
            
            @Override
            public void onError(String error) {
                synchronized (todosResultados) {
                    // Log do erro mas não interromper a busca - continuar com outros resultados
                    android.util.Log.e("SelecaoObraActivity", "Erro ao buscar obras de arte: " + error);
                    buscaCompletas[0]++;
                    if (buscaCompletas[0] == totalBuscas) {
                        combinarResultados.run();
                    }
                }
            }
        });
    }

    private void exibirResultados(List<ModeloFilme> resultados) {
        mostrarLoading(false);
        esconderMensagemErro();
        adapter.atualizarLista(resultados);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void mostrarLoading(boolean mostrar) {
        progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        if (mostrar) {
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void mostrarMensagemErro(boolean mostrar) {
        mensagemErro.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        if (mostrar) {
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void esconderMensagemErro() {
        mensagemErro.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(ModeloFilme obra) {
        // Retornar a obra selecionada para a tela anterior
        Intent resultIntent = new Intent();
        resultIntent.putExtra("obra_selecionada", obra);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
} 