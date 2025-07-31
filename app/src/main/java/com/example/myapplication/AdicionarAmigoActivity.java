package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class AdicionarAmigoActivity extends AppCompatActivity {

    private EditText searchBar;
    private RecyclerView usersList;
    private LinearLayout emptyState;
    private UsuarioAdapter adapter;
    private List<Usuario> allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_amigo);

        // Inicializar views
        initializeViews();
        
        // Configurar listeners
        setupListeners();
        
        // Carregar dados de exemplo
        loadSampleData();
        
        // Configurar RecyclerView
        setupRecyclerView();
        
        // Animar elementos na entrada
        animateElementsOnStart();
    }

    private void initializeViews() {
        searchBar = findViewById(R.id.search_bar);
        usersList = findViewById(R.id.users_list);
        emptyState = findViewById(R.id.empty_state);
        
        ImageView btnVoltar = findViewById(R.id.btn_voltar);
        btnVoltar.setOnClickListener(v -> {
            AppAnimationUtils.animateButtonClick(btnVoltar, () -> {
                finish();
            });
        });
    }

    private void setupListeners() {
        // Listener da barra de pesquisa
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }



    private void loadSampleData() {
        allUsers = new ArrayList<>();
        
        allUsers.add(new Usuario("Ana Silva", "Filmes • Livros", R.drawable.user_white));
        allUsers.add(new Usuario("Carlos Santos", "Arte • Filmes", R.drawable.user_white));
        allUsers.add(new Usuario("Maria Costa", "Livros • Arte", R.drawable.user_white));
        allUsers.add(new Usuario("João Oliveira", "Filmes", R.drawable.user_white));
        allUsers.add(new Usuario("Sofia Pereira", "Arte • Livros", R.drawable.user_white));
        allUsers.add(new Usuario("Lucas Ferreira", "Filmes • Arte", R.drawable.user_white));
        allUsers.add(new Usuario("Isabella Rodrigues", "Livros", R.drawable.user_white));
        allUsers.add(new Usuario("Pedro Almeida", "Filmes • Livros • Arte", R.drawable.user_white));
    }

    private void setupRecyclerView() {
        adapter = new UsuarioAdapter(allUsers, this::onAddFriendClick);
        usersList.setLayoutManager(new LinearLayoutManager(this));
        usersList.setAdapter(adapter);
    }

    private void filterUsers(String searchTerm) {
        List<Usuario> filteredUsers = new ArrayList<>();
        
        for (Usuario user : allUsers) {
            boolean matchesSearch = searchTerm.isEmpty() || 
                user.getNome().toLowerCase().contains(searchTerm.toLowerCase()) ||
                user.getInteresses().toLowerCase().contains(searchTerm.toLowerCase());
                
            if (matchesSearch) {
                filteredUsers.add(user);
            }
        }
        
        adapter.updateUsers(filteredUsers);
        updateEmptyState(filteredUsers.isEmpty());
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            usersList.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            AppAnimationUtils.animateFadeIn(emptyState);
        } else {
            emptyState.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
        }
    }

    private void onAddFriendClick(Usuario usuario) {
        // TODO: Implementar funcionalidade de adicionar amigo
        Toast.makeText(this, "Solicitação enviada para " + usuario.getNome(), Toast.LENGTH_SHORT).show();
    }

    private void animateElementsOnStart() {
        // Animar header
        AppAnimationUtils.animateFadeIn(findViewById(R.id.header));
        
        // Animar barra de pesquisa
        AppAnimationUtils.animateSlideUp(searchBar);
        
        // Animar lista
        AppAnimationUtils.animateFadeIn(usersList);
    }

    // Classe interna para representar um usuário
    public static class Usuario {
        private String nome;
        private String interesses;
        private int avatarResId;

        public Usuario(String nome, String interesses, int avatarResId) {
            this.nome = nome;
            this.interesses = interesses;
            this.avatarResId = avatarResId;
        }

        public String getNome() { return nome; }
        public String getInteresses() { return interesses; }
        public int getAvatarResId() { return avatarResId; }
    }

    // Interface para callback do botão adicionar
    public interface OnAddFriendClickListener {
        void onAddFriendClick(Usuario usuario);
    }
} 