package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
        LinearLayout cardProfile = findViewById(R.id.card_profile);
        LinearLayout profileDetails = findViewById(R.id.option_profile_details);
        LinearLayout password = findViewById(R.id.option_password);
        LinearLayout notifications = findViewById(R.id.option_notifications);
        LinearLayout about = findViewById(R.id.option_about);
        LinearLayout acessibilidade = findViewById(R.id.option_acessibilidade);
        LinearLayout help = findViewById(R.id.option_help);
        LinearLayout deactivate = findViewById(R.id.sair_btn);

        // Card de perfil (primeiro card com foto, nome, etc) -> ExibirPerfil
        cardProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfActivity.this, ExibirPerfil.class);
                startActivity(intent);
            }
        });

        // Detalhes do perfil -> Alt_perfil
        profileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent intent = new Intent(ConfActivity.this, NotificacoesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                Intent intent = new Intent(ConfActivity.this, AjudaFaqActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

        if (userId == null || userId.isEmpty()) {
            Log.e("Perfil", "user_id não encontrado nas SharedPreferences");
            if (profile_name != null) profile_name.setText("Nome do Usuário");
            if (profile_role != null) profile_role.setText("@usuario");
            return;
        }

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
                        if (profile_name != null && nome != null && !nome.isEmpty()) {
                            profile_name.setText(nome);
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
							if (profile_role != null && usuario != null && !usuario.isEmpty()) {
								profile_role.setText(usuario);
							}
						} else {
							Log.w("Perfil", "receber_usuario retornou sucesso=false: " + json.optString("message"));
						}
					} catch (Exception jsEx) {
						// Fallback: tentar extrair o campo usuario por regex simples
						java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"usuario\"\\s*:\\s*\"([^\"]+)\"").matcher(jsonCandidate);
						if (m.find()) {
							String usuario = m.group(1);
							if (profile_role != null && usuario != null && !usuario.isEmpty()) {
								profile_role.setText(usuario);
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
