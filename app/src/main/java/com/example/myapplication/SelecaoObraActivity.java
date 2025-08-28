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
    private TextView filtroTodos, filtroFilmes, filtroSeries, filtroLivros;
    private String filtroAtivo = "todos";
    
    // API Manager
    private TMDBManager tmdbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_obra);

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
    }

    private void selecionarFiltro(String filtro, TextView filtroSelecionado) {
        // Resetar todos os filtros
        filtroTodos.setBackgroundTintList(null);
        filtroFilmes.setBackgroundTintList(null);
        filtroSeries.setBackgroundTintList(null);
        filtroLivros.setBackgroundTintList(null);

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
        
        // Carregar filmes populares
        tmdbManager.getPopularMovies(new TMDBManager.PopularCallback() {
            @Override
            public void onSuccess(List<ModeloFilme> filmes) {
                // Depois carregar séries populares
                tmdbManager.getPopularTVShows(new TMDBManager.PopularCallback() {
                    @Override
                    public void onSuccess(List<ModeloFilme> series) {
                        runOnUiThread(() -> {
                            mostrarLoading(false);
                            
                            // Combinar resultados
                            List<ModeloFilme> todasObras = new ArrayList<>();
                            todasObras.addAll(filmes);
                            todasObras.addAll(series);
                            
                            if (todasObras.isEmpty()) {
                                mostrarMensagemErro(true);
                                mensagemErro.setText("Nenhuma obra popular encontrada");
                            } else {
                                exibirResultados(todasObras);
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            mostrarLoading(false);
                            if (!filmes.isEmpty()) {
                                exibirResultados(filmes);
                            } else {
                                mostrarMensagemErro(true);
                                mensagemErro.setText("Erro ao carregar obras: " + error);
                            }
                        });
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    mostrarLoading(false);
                    mostrarMensagemErro(true);
                    mensagemErro.setText("Erro ao carregar obras: " + error);
                });
            }
        });
    }

    private void realizarBusca(String termo) {
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
                
            default: // "todos" ou "livros"
                // Buscar filmes e séries
                tmdbManager.searchMovies(termo, new TMDBManager.SearchCallback() {
                    @Override
                    public void onSuccess(List<ModeloFilme> filmes) {
                        tmdbManager.searchTVShows(termo, new TMDBManager.SearchCallback() {
                            @Override
                            public void onSuccess(List<ModeloFilme> series) {
                                runOnUiThread(() -> {
                                    mostrarLoading(false);
                                    
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
                                    if (!filmes.isEmpty()) {
                                        exibirResultados(filmes);
                                    } else {
                                        mostrarMensagemErro(true);
                                        mensagemErro.setText("Erro na busca: " + error);
                                    }
                                });
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
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
                                });
                            }
                        });
                    }
                });
                break;
        }
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