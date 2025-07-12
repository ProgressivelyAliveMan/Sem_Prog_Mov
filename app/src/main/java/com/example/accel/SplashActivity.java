package com.example.accel; // Asegúrate de que este sea tu paquete

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Referenciar las dos nuevas imágenes
        ImageView imagenBandera = findViewById(R.id.imagen_bandera);
        ImageView imagenLogoUtp = findViewById(R.id.imagen_logo_utp);

        // Cargar la animación
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Aplicar la animación a ambas imágenes
        imagenBandera.startAnimation(fadeIn);
        imagenLogoUtp.startAnimation(fadeIn);

        // El Handler para cambiar de actividad permanece igual
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Iniciar la actividad del Quiz después de la duración establecida
                Intent intent = new Intent(SplashActivity.this, QuizActivity.class);
                startActivity(intent);
                finish(); // Cierra esta actividad para que el usuario no pueda volver a ella
            }
        }, SPLASH_DURATION);
    }
}
