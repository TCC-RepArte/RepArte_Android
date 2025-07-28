package com.example.myapplication;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ExibirPerfil extends AppCompatActivity {

    private static final String TAG = "ExibirPerfil"; // Tag para Logs

    private TextView textViewPostagem;
    private TextView textViewComentario;
    private TextView textViewSobreMim;
    private View indicatorView;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exibir_perfil);

        constraintLayout = findViewById(R.id.main);
        textViewPostagem = findViewById(R.id.textViewPostagem);
        textViewComentario = findViewById(R.id.textViewComentarios);
        textViewSobreMim = findViewById(R.id.textViewSobreMim);
        indicatorView = findViewById(R.id.indicatorView);

        if (constraintLayout == null) {
            Log.e(TAG, "ConstraintLayout 'main' não encontrado!");
            return;
        }
        if (indicatorView == null) {
            Log.e(TAG, "IndicatorView 'indicatorView' não encontrado!");
        }


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
