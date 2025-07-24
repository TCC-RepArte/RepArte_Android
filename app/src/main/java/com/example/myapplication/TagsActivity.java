package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TagsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags);

        Button btnContinuar = findViewById(R.id.btnContinue);
        btnContinuar.setOnClickListener(v -> {
            Intent intent = new Intent(TagsActivity.this, FeedbackActivity.class);
            intent.putExtra("tipo_feedback", "sucesso");
            startActivity(intent);
            finish();
        });
    }
} 