package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.myapplication.api.TMDBManager;
import java.util.ArrayList;
import java.util.List;

public class BuscaActivity extends AppCompatActivity implements AdapterResultadosBusca.OnItemClickListener {
    
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView mensagemErro;
    private TextView termoPesquisado;
    private LinearLayout containerResultados;
    private AdapterResultadosBusca adapter;
    private List<ModeloFilme> listaFilmes;
    
    // Filtros
    private TextView filtroTodos, filtroFilmes, filtroSeries, filtroLivros;
    private String filtroAtivo = "todos";
    
    // API Manager
    private TMDBManager tmdbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.busca_resultados);

        // Inicializar views
        inicializarViews();
        
        // Inicializar API Manager
        tmdbManager = new TMDBManager();
        
        // Configurar RecyclerView
        configurarRecyclerView();
        
        // Configurar SearchView
        configurarSearchView();
        
        // Configurar botão voltar
        configurarBotaoVoltar();
        
        // Configurar filtros
        configurarFiltros();
        
        // Configurar seções
        configurarSecoes();
        
        // Inicializar "Todos" como selecionado
        inicializarFiltroPadrao();
        
        // Verificar se recebeu termo de busca da tela anterior
        verificarTermoBusca();
    }

    private void inicializarViews() {
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_resultados);
        progressBar = findViewById(R.id.progress_bar);
        mensagemErro = findViewById(R.id.mensagem_erro);
        termoPesquisado = findViewById(R.id.termo_pesquisado);
        containerResultados = findViewById(R.id.container_resultados);
        
        // Filtros
        filtroTodos = findViewById(R.id.filtro_todos);
        filtroFilmes = findViewById(R.id.filtro_filmes);
        filtroSeries = findViewById(R.id.filtro_series);
        filtroLivros = findViewById(R.id.filtro_livros);
    }

    private void configurarRecyclerView() {
        listaFilmes = new ArrayList<>();
        adapter = new AdapterResultadosBusca(this, listaFilmes);
        adapter.setOnItemClickListener(this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void configurarSearchView() {
        // Garantir que a SearchView esteja sempre expandida e editável
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
                // Busca em tempo real (opcional)
                // if (newText != null && newText.length() >= 3) {
                //     realizarBusca(newText.trim());
                // }
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
        // Configurar cliques nos filtros
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
    }

    private void selecionarFiltro(String filtro, TextView filtroSelecionado) {
        // Resetar todos os filtros para cor neutra (sem tint)
        filtroTodos.setBackgroundTintList(null);
        filtroFilmes.setBackgroundTintList(null);
        filtroSeries.setBackgroundTintList(null);
        filtroLivros.setBackgroundTintList(null);

        // Selecionar o filtro clicado com cor AMARELA
        filtroSelecionado.setBackgroundTintList(getResources().getColorStateList(R.color.amarelo_filtro));
        filtroAtivo = filtro;

        // Aplicar filtro se houver resultados
        if (listaFilmes.size() > 0) {
            aplicarFiltro();
        }
    }

    private void aplicarFiltro() {
        // TODO: Implementar filtro real quando API estiver pronta
        // Por enquanto, apenas simular
        android.widget.Toast.makeText(this, "Filtro aplicado: " + filtroAtivo, android.widget.Toast.LENGTH_SHORT).show();
    }

    private void inicializarFiltroPadrao() {
        // "Todos" já começa selecionado (amarelo)
        filtroTodos.setBackgroundTintList(getResources().getColorStateList(R.color.amarelo_filtro));
        filtroAtivo = "todos";
    }

    private void configurarSecoes() {
        // Carregar filmes populares da API
        carregarFilmesPopulares();
        
        // Configurar seção "Livros Fuvest" (mantém os exemplos)
        configurarCliqueSecao(R.id.livro_1, "Dom Casmurro", "1899", "Livro", 9.0, 50000);
        configurarCliqueSecao(R.id.livro_2, "Grande Sertão", "1956", "Livro", 8.9, 30000);
        
        // Carregar séries populares da API
        carregarSeriesPopulares();
    }

    private void configurarCliqueSecao(int id, String titulo, String ano, String tipo, double avaliacao, int votos) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuscaActivity.this, DetalhesFilmeActivity.class);
                intent.putExtra("titulo", titulo);
                intent.putExtra("ano", ano);
                intent.putExtra("tipo", tipo);
                intent.putExtra("avaliacao", avaliacao);
                intent.putExtra("votos", votos);
                startActivity(intent);
            }
        });
    }

    private void verificarTermoBusca() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("termo_busca")) {
            String termo = intent.getStringExtra("termo_busca");
            if (termo != null && !termo.isEmpty()) {
                searchView.setQuery(termo, false);
                realizarBusca(termo);
            }
        }
    }

    private void realizarBusca(String termo) {
        // Mostrar loading
        mostrarLoading(true);
        
        // Esconder seções e mostrar resultados
        esconderSecoes();
        mostrarResultados();
        
        // Atualizar texto do termo pesquisado
        termoPesquisado.setText("Resultados para: " + termo);
        
        // Realizar busca na API TMDB
        realizarBuscaAPI(termo);
    }
    
    private void realizarBuscaAPI(String termo) {
        // Determinar tipo de busca baseado no filtro ativo
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
                            Toast.makeText(BuscaActivity.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                break;
                
            case "series":
                tmdbManager.searchTVShows(termo, new TMDBManager.SearchCallback() {
                    @Override
                    public void onSuccess(List<ModeloFilme> filmes) {
                        runOnUiThread(() -> {
                            mostrarLoading(false);
                            if (filmes.isEmpty()) {
                                mostrarMensagemErro(true);
                                mensagemErro.setText("Nenhuma série encontrada para: " + termo);
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
                            Toast.makeText(BuscaActivity.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                break;
                
            default: // "todos" ou "livros"
                // Buscar filmes e séries
                tmdbManager.searchMovies(termo, new TMDBManager.SearchCallback() {
                    @Override
                    public void onSuccess(List<ModeloFilme> filmes) {
                        // Depois buscar séries
                        tmdbManager.searchTVShows(termo, new TMDBManager.SearchCallback() {
                            @Override
                            public void onSuccess(List<ModeloFilme> series) {
                                runOnUiThread(() -> {
                                    mostrarLoading(false);
                                    
                                    // Combinar resultados
                                    List<ModeloFilme> todosResultados = new ArrayList<>();
                                    todosResultados.addAll(filmes);
                                    todosResultados.addAll(series);
                                    
                                    if (todosResultados.isEmpty()) {
                                        mostrarMensagemErro(true);
                                        mensagemErro.setText("Nenhum resultado encontrado para: " + termo);
                                    } else {
                                        exibirResultados(todosResultados);
                                    }
                                });
                            }
                            
                            @Override
                            public void onError(String error) {
                                runOnUiThread(() -> {
                                    mostrarLoading(false);
                                    // Se pelo menos filmes foram encontrados, mostrar eles
                                    if (!filmes.isEmpty()) {
                                        exibirResultados(filmes);
                                    } else {
                                        mostrarMensagemErro(true);
                                        mensagemErro.setText("Erro na busca: " + error);
                                        Toast.makeText(BuscaActivity.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        // Se filmes falharam, tentar só séries
                        tmdbManager.searchTVShows(termo, new TMDBManager.SearchCallback() {
                            @Override
                            public void onSuccess(List<ModeloFilme> series) {
                                runOnUiThread(() -> {
                                    mostrarLoading(false);
                                    if (series.isEmpty()) {
                                        mostrarMensagemErro(true);
                                        mensagemErro.setText("Nenhum resultado encontrado para: " + termo);
                                    } else {
                                        exibirResultados(series);
                                    }
                                });
                            }
                            
                            @Override
                            public void onError(String seriesError) {
                                runOnUiThread(() -> {
                                    mostrarLoading(false);
                                    mostrarMensagemErro(true);
                                    mensagemErro.setText("Erro na busca: " + error);
                                    Toast.makeText(BuscaActivity.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    }
                });
                break;
        }
    }

    private void esconderSecoes() {
        // Esconder todas as seções quando buscar
        View[] secoes = {
            (View) findViewById(R.id.filtro_todos).getParent().getParent(), // Filtros
            (View) findViewById(R.id.filme_1).getParent().getParent().getParent(), // Filmes em alta
            (View) findViewById(R.id.livro_1).getParent().getParent().getParent(), // Livros Fuvest
            (View) findViewById(R.id.serie_1).getParent().getParent().getParent() // Séries populares
        };
        
        for (View secao : secoes) {
            if (secao != null) {
                secao.setVisibility(View.GONE);
            }
        }
    }

    private void mostrarResultados() {
        containerResultados.setVisibility(View.VISIBLE);
    }

    private void simularBusca(String termo) {
        // Simular delay de rede
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Dados de exemplo
                List<ModeloFilme> resultados = new ArrayList<>();
                
                if (termo.toLowerCase().contains("batman")) {
                    resultados.add(new ModeloFilme(1, "Batman Begins", "/batman.jpg", "2005-06-15", 8.2, "movie"));
                    resultados.add(new ModeloFilme(2, "The Dark Knight", "/dark_knight.jpg", "2008-07-18", 9.0, "movie"));
                    resultados.add(new ModeloFilme(3, "The Dark Knight Rises", "/dark_knight_rises.jpg", "2012-07-20", 8.4, "movie"));
                } else if (termo.toLowerCase().contains("marvel")) {
                    resultados.add(new ModeloFilme(4, "Avengers: Endgame", "/avengers.jpg", "2019-04-26", 8.4, "movie"));
                    resultados.add(new ModeloFilme(5, "Spider-Man: No Way Home", "/spiderman.jpg", "2021-12-17", 8.2, "movie"));
                } else if (termo.toLowerCase().contains("fuvest")) {
                    resultados.add(new ModeloFilme(6, "Dom Casmurro", "/dom_casmurro.jpg", "1899-01-01", 9.0, "book"));
                    resultados.add(new ModeloFilme(7, "Grande Sertão: Veredas", "/grande_sertao.jpg", "1956-01-01", 8.9, "book"));
                } else {
                    // Resultados genéricos
                    resultados.add(new ModeloFilme(8, "Inception", "/inception.jpg", "2010-07-16", 8.8, "movie"));
                    resultados.add(new ModeloFilme(9, "Interstellar", "/interstellar.jpg", "2014-11-07", 8.6, "movie"));
                    resultados.add(new ModeloFilme(10, "The Matrix", "/matrix.jpg", "1999-03-31", 8.7, "movie"));
                }
                
                // Configurar votos para os filmes de exemplo
                for (ModeloFilme filme : resultados) {
                    filme.setVotos((int) (Math.random() * 1000000) + 10000);
                }
                
                exibirResultados(resultados);
            }
        }, 1500); // 1.5 segundos de delay
    }

    private void exibirResultados(List<ModeloFilme> resultados) {
        mostrarLoading(false);
        
        if (resultados.isEmpty()) {
            mostrarMensagemErro(true);
            adapter.atualizarLista(new ArrayList<>());
        } else {
            mostrarMensagemErro(false);
            adapter.atualizarLista(resultados);
        }
    }

    private void mostrarLoading(boolean mostrar) {
        progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        if (mostrar) {
            containerResultados.setVisibility(View.GONE);
        }
    }

    private void mostrarMensagemErro(boolean mostrar) {
        mensagemErro.setVisibility(mostrar ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(ModeloFilme filme) {
        try {
            // Navegar para tela de detalhes do filme
            Intent intent = new Intent(this, DetalhesFilmeActivity.class);
            intent.putExtra("filme_id", filme.getId());
            intent.putExtra("titulo", filme.getTitulo());
            intent.putExtra("ano", filme.getAno());
            intent.putExtra("tipo", filme.getTipo()); // Usar tipo original (movie/tv)
            intent.putExtra("avaliacao", filme.getAvaliacao());
            intent.putExtra("votos", filme.getVotos());
            intent.putExtra("poster_path", filme.getPosterPath());
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao abrir detalhes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarFilmesPopulares() {
        tmdbManager.getPopularMovies(new TMDBManager.PopularCallback() {
            @Override
            public void onSuccess(List<ModeloFilme> filmes) {
                runOnUiThread(() -> {
                    if (filmes.size() >= 3) {
                        configurarFilmePopular(R.id.filme_1, filmes.get(0));
                        configurarFilmePopular(R.id.filme_2, filmes.get(1));
                        configurarFilmePopular(R.id.filme_3, filmes.get(2));
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                android.util.Log.e("BuscaActivity", "Erro ao carregar filmes populares: " + error);
                // Em caso de erro, usar dados de exemplo
                configurarCliqueSecao(R.id.filme_1, "Inception", "2010", "Filme", 8.8, 1000000);
                configurarCliqueSecao(R.id.filme_2, "Interstellar", "2014", "Filme", 8.6, 800000);
                configurarCliqueSecao(R.id.filme_3, "The Matrix", "1999", "Filme", 8.7, 1200000);
            }
        });
    }

    private void carregarSeriesPopulares() {
        tmdbManager.getPopularTVShows(new TMDBManager.PopularCallback() {
            @Override
            public void onSuccess(List<ModeloFilme> series) {
                runOnUiThread(() -> {
                    if (series.size() >= 2) {
                        configurarSeriePopular(R.id.serie_1, series.get(0));
                        configurarSeriePopular(R.id.serie_2, series.get(1));
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                android.util.Log.e("BuscaActivity", "Erro ao carregar séries populares: " + error);
                // Em caso de erro, usar dados de exemplo
                configurarCliqueSecao(R.id.serie_1, "Breaking Bad", "2008", "Série", 9.5, 2000000);
                configurarCliqueSecao(R.id.serie_2, "Stranger Things", "2016", "Série", 8.7, 1500000);
            }
        });
    }

    private void configurarFilmePopular(int viewId, ModeloFilme filme) {
        View view = findViewById(viewId);
        if (view != null) {
            // Atualizar título (primeiro TextView dentro do LinearLayout)
            TextView txtTitulo = (TextView) ((LinearLayout) view).getChildAt(1);
            if (txtTitulo != null) {
                txtTitulo.setText(filme.getTitulo());
            }
            
            // Atualizar imagem (primeiro ImageView dentro do LinearLayout)
            ImageView imgCapa = (ImageView) ((LinearLayout) view).getChildAt(0);
            if (imgCapa != null && filme.getPosterPath() != null) {
                String fullPosterUrl = "https://image.tmdb.org/t/p/w200" + filme.getPosterPath();
                Glide.with(this)
                    .load(fullPosterUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(imgCapa);
            }
            
            // Configurar clique
            view.setOnClickListener(v -> {
                Intent intent = new Intent(BuscaActivity.this, DetalhesFilmeActivity.class);
                intent.putExtra("filme_id", filme.getId());
                intent.putExtra("titulo", filme.getTitulo());
                intent.putExtra("ano", filme.getAno());
                intent.putExtra("tipo", filme.getTipo());
                intent.putExtra("avaliacao", filme.getAvaliacao());
                intent.putExtra("votos", filme.getVotos());
                intent.putExtra("poster_path", filme.getPosterPath());
                startActivity(intent);
            });
        }
    }

    private void configurarSeriePopular(int viewId, ModeloFilme serie) {
        View view = findViewById(viewId);
        if (view != null) {
            // Atualizar título (primeiro TextView dentro do LinearLayout)
            TextView txtTitulo = (TextView) ((LinearLayout) view).getChildAt(1);
            if (txtTitulo != null) {
                txtTitulo.setText(serie.getTitulo());
            }
            
            // Atualizar imagem (primeiro ImageView dentro do LinearLayout)
            ImageView imgCapa = (ImageView) ((LinearLayout) view).getChildAt(0);
            if (imgCapa != null && serie.getPosterPath() != null) {
                String fullPosterUrl = "https://image.tmdb.org/t/p/w200" + serie.getPosterPath();
                Glide.with(this)
                    .load(fullPosterUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(imgCapa);
            }
            
            // Configurar clique
            view.setOnClickListener(v -> {
                Intent intent = new Intent(BuscaActivity.this, DetalhesFilmeActivity.class);
                intent.putExtra("filme_id", serie.getId());
                intent.putExtra("titulo", serie.getTitulo());
                intent.putExtra("ano", serie.getAno());
                intent.putExtra("tipo", serie.getTipo());
                intent.putExtra("avaliacao", serie.getAvaliacao());
                intent.putExtra("votos", serie.getVotos());
                intent.putExtra("poster_path", serie.getPosterPath());
                startActivity(intent);
            });
        }
    }
} 