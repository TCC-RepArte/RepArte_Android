package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button; // Para o botão Editar Perfil
import android.widget.ImageButton; // Para o botão Voltar
import android.widget.TextView;
import android.widget.ImageView;
import com.example.myapplication.api.ApiService;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.bitmap.Transform;

import org.json.JSONException;
import org.json.JSONObject;

public class ExibirPerfil extends AppCompatActivity {

    private static final String TAG = "ExibirPerfil"; // Tag para Logs
    private ApiService apiService;
    private TextView textViewPostagem;
    private TextView textViewComentario;
    private TextView textViewSobreMim;
    private ImageView profileImageView;
    private Button btnSalvar;
    private View indicatorView;
    private ConstraintLayout constraintLayout;

    private ImageButton btnVoltar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exibir_perfil);

        apiService = new ApiService(this);

        constraintLayout = findViewById(R.id.main);
        textViewPostagem = findViewById(R.id.textViewPostagem);
        textViewComentario = findViewById(R.id.textViewComentarios);
        textViewSobreMim = findViewById(R.id.textViewSobreMim);
        indicatorView = findViewById(R.id.indicatorView);
        profileImageView = findViewById(R.id.imageView4);
        btnSalvar = findViewById(R.id.btnSalvar);

        btnVoltar = findViewById(R.id.btn_Voltar);

        if (constraintLayout == null) {
            Log.e(TAG, "ConstraintLayout 'main' não encontrado!");
            return;
        }
        if (indicatorView == null) {
            Log.e(TAG, "IndicatorView 'indicatorView' não encontrado!");
        }

        String userId = getSharedPreferences("RepArte", MODE_PRIVATE).getString("user_id", null);
            String fotoUrl = apiService.getFotoPerfilUrl(userId);
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


        // Configura o botão de salvar
        if (btnSalvar != null) {
            btnSalvar.setOnClickListener(v -> {
                Intent intent = new Intent(ExibirPerfil.this, Alt_perfil.class);
                String currentUserId = getSharedPreferences("RepArte", MODE_PRIVATE).getString("user_id", null);
                if (currentUserId != null) {
                    intent.putExtra("USER_ID_EXTRA", currentUserId); // Opcional: passa o ID para a tela de edição
                }
                startActivity(intent);

            });
        } else {
            Log.e(TAG, "Botão 'btnSalvar' não encontrado para configurar o OnClickListener.");
        }

        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(ExibirPerfil.this, ConfActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });


        // Configura o estado inicial e listeners
        if (textViewPostagem != null) {
            if (indicatorView != null) {
                updateIndicator(textViewPostagem);
            }
            textViewPostagem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateIndicator(textViewPostagem);
                    // TODO: Lógica para mostrar conteúdo de Postagens
                }
            });
        } else {
            Log.w(TAG, "TextView 'textViewPostagem' não encontrado!");
        }

        if (textViewComentario != null) {
            textViewComentario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateIndicator(textViewComentario);
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
                    updateIndicator(textViewSobreMim);
                    // TODO: Lógica para mostrar conteúdo de Sobre Mim
                }
            });
        } else {
            Log.w(TAG, "TextView 'textViewSobreMim' não encontrado!");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void updateIndicator(TextView selectedTextView) {
        // Verificações de nulo para segurança
        if (indicatorView == null || selectedTextView == null || constraintLayout == null) {
            Log.w(TAG, "updateIndicator: Uma ou mais Views são nulas! Não é possível atualizar o indicador.");
            return;
        }

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        // 1. Conectar o TOPO do 'indicatorView' à BASE do 'selectedTextView'
        constraintSet.connect(
                indicatorView.getId(),
                ConstraintSet.TOP,
                selectedTextView.getId(),
                ConstraintSet.BOTTOM,
                dpToPx(4) // Sua margem de 4dp
        );

        // 2. Alinhar o INÍCIO do 'indicatorView' com o INÍCIO do 'selectedTextView'
        constraintSet.connect(
                indicatorView.getId(),
                ConstraintSet.START,
                selectedTextView.getId(),
                ConstraintSet.START
        );

        // 3. Alinhar o FIM do 'indicatorView' com o FIM do 'selectedTextView'
        constraintSet.connect(
                indicatorView.getId(),
                ConstraintSet.END,
                selectedTextView.getId(),
                ConstraintSet.END
        );

        // 4. Definir a LARGURA do 'indicatorView' para preencher o espaço entre suas constraints START e END
        constraintSet.constrainWidth(
                indicatorView.getId(),
                ConstraintSet.MATCH_CONSTRAINT
        );

        constraintSet.applyTo(constraintLayout);
    }

    // Função utilitária para converter dp em pixels
    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        // Usar displayMetrics.density é geralmente mais simples e recomendado
        return Math.round(dp * displayMetrics.density);

    }
}
