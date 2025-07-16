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
                progressBar.setProgress(100);
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
                // Cuando la animación de salida termina, inicia la siguiente actividad
                // NOTA: Asumo que la siguiente pantalla es MenuActivity, cámbialo si es necesario
                Intent intent = new Intent(SplashActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Inicia la animación en el contenedor principal
        mainContainer.startAnimation(fadeOut);
    }
}