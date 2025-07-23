package com.example.accel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button btnPruebaMatematica = findViewById(R.id.btn_prueba_matematica);
        Button btnAcelerometro = findViewById(R.id.btn_acelerometro);
        Button btnDetectorMetales = findViewById(R.id.btn_detector_metales);

        btnPruebaMatematica.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, QuizActivity.class);
            startActivity(intent);
        });

        btnAcelerometro.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, AccelerometerActivity.class);
            startActivity(intent);
        });

        btnDetectorMetales.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MagnetometerActivity.class);
            startActivity(intent);
        });
    }
}
