package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

public class Tela extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela1);

        //declarar
        ImageView perfil;
        SearchView pesquisar;

        //id
        perfil = findViewById(R.id.perfil);
        pesquisar = findViewById(R.id.pesquisar);

        //evento do botao perfil
        perfil.setOnClickListener(view -> {
            Intent intent = new Intent(Tela.this, ConfActivity.class);
            startActivity(intent);
        });

        //evento da barra de pesquisa
        pesquisar.setOnClickListener(view -> {
            // Navegar para a tela de busca
            Intent intent = new Intent(Tela.this, BuscaActivity.class);
            startActivity(intent);
        });

        // Configurar a SearchView para abrir a tela de busca quando clicar
        pesquisar.setOnSearchClickListener(view -> {
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
    }
}