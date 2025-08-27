package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.api.ApiService;
import androidx.appcompat.app.AppCompatActivity;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.bitmap.Transform;

import org.json.JSONObject;

public class ConfActivity extends AppCompatActivity {

    private ApiService apiService;
    private TextView profile_name;
    private TextView profile_role;
    private ImageView profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf);

        apiService = new ApiService(this);

        // Inicializar as VARIÁVEIS DE MEMBRO da classe
        profile_image = findViewById(R.id.profile_image);
        profile_name = findViewById(R.id.profile_name);
        profile_role = findViewById(R.id.profile_role);

        // Preparar cliques dos itens
        LinearLayout profileDetails = findViewById(R.id.option_profile_details);
        LinearLayout password = findViewById(R.id.option_password);
        LinearLayout notifications = findViewById(R.id.option_notifications);
        LinearLayout about = findViewById(R.id.option_about);
        LinearLayout acessibilidade = findViewById(R.id.option_acessibilidade);
        LinearLayout help = findViewById(R.id.option_help);
        LinearLayout deactivate = findViewById(R.id.option_deactivate);

        profileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfActivity.this, ExibirPerfil.class);
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
                Intent intent = new Intent(ConfActivity.this, SobreAppActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        acessibilidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfActivity.this, AcessibilidadeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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


        loadUserProfileData();
    }


    private void loadUserProfileData() {
        String userId = getSharedPreferences("RepArte", MODE_PRIVATE).getString("user_id", null);

        String fotoUrl = apiService.getFotoPerfilUrl(userId);
        Log.d("Perfil", "URL da foto: " + fotoUrl);

        if (profile_image != null) {
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
                    .intoImageView(profile_image) // Usando a variável de membro profile_image
                    .setCallback((exception, result) -> {
                        if (exception != null) {
                            Log.e("Perfil", "Erro ao carregar imagem circular", exception);
                            profile_image.setImageResource(R.drawable.user_white); // Fallback na variável de membro
                        } else {
                            Log.d("Perfil", "Imagem circular carregada com sucesso");
                        }
                    });
        } else {
            Log.e("Perfil", "ImageView profile_image é nula. Não foi possível carregar a foto.");
        }
    }

}
