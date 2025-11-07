package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class AjudaFaqActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajuda_faq);

        // Botão voltar
        ImageView btnVoltar = findViewById(R.id.btn_voltar);
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(view -> {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            });
        }

        // Ícone Gmail - abre app de email para escrever
        ImageView icGmail = findViewById(R.id.ic_gmail);
        if (icGmail != null) {
            icGmail.setOnClickListener(view -> {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:equipereparte@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"equipereparte@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contato - RepArte");
                
                try {
                    startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    // Se não houver app de email, tenta abrir o navegador
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com/mail/?view=cm&to=equipereparte@gmail.com"));
                    startActivity(browserIntent);
                }
            });
        }

        // Ícone Instagram - abre perfil no Instagram
        ImageView icInstagram = findViewById(R.id.ic_instagram);
        if (icInstagram != null) {
            icInstagram.setOnClickListener(view -> {
                Uri instagramUri = Uri.parse("https://www.instagram.com/reparte_3y/");
                Intent instagramIntent = new Intent(Intent.ACTION_VIEW, instagramUri);
                
                // Tenta abrir no app do Instagram primeiro
                instagramIntent.setPackage("com.instagram.android");
                
                try {
                    startActivity(instagramIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Se o app não estiver instalado, abre no navegador
                    instagramIntent.setPackage(null);
                    startActivity(instagramIntent);
                }
            });
        }
    }
}

