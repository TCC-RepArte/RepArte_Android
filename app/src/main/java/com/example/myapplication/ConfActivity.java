package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class ConfActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf);

        // Preparar cliques dos itens
        LinearLayout profileDetails = findViewById(R.id.option_profile_details);
        LinearLayout password = findViewById(R.id.option_password);
        LinearLayout notifications = findViewById(R.id.option_notifications);
        LinearLayout about = findViewById(R.id.option_about);
        LinearLayout help = findViewById(R.id.option_help);
        LinearLayout deactivate = findViewById(R.id.option_deactivate);

        profileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar para a tela de detalhes do perfil
                Intent intent = new Intent(ConfActivity.this, Alt_perfil.class);
                startActivity(intent);
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegação futura: Senha
            }
        });
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegação futura: Notificações
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegação futura: Sobre o aplicativo
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegação futura: Ajuda/FAQ
            }
        });
        deactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegação futura: Desativar conta
            }
        });
    }
} 