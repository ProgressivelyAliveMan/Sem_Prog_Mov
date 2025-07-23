package com.example.accel; // Asegúrate de que este sea tu paquete

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 segundos
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);

        iniciarCarga();
    }

    private void iniciarCarga() {
        new CountDownTimer(SPLASH_DURATION, 30) { // Actualiza cada 30ms
            @Override
            public void onTick(long tiempoFin) {
                // Calcula el progreso en un rango de 0 a 100
                int progress = (int) (((SPLASH_DURATION - tiempoFin) / (double) SPLASH_DURATION) * 100);
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                // Cuando el contador termina, la barra llega al 100%
                progressBar.setProgress(100);
                // Ahora, inicia la animación de salida
                iniciarFadeOut();
            }
        }.start();
    }

    private void iniciarFadeOut() {
        // Carga la animación de fade_out
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        LinearLayout mainContainer = findViewById(R.id.main_content_container);

        // Define qué hacer cuando la animación termine
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Cuando la animación de salida termina, inicia la siguiente actividad.
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Cierra esta actividad para que no se pueda volver a ella
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Inicia la animación en el contenedor principal que tiene todos los elementos
        mainContainer.startAnimation(fadeOut);
    }
}