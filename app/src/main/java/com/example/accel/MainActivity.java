package com.example.accel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    // Duración de la carga secundaria (después del splash nativo)
    private static final int SECONDARY_LOADING_DURATION = 3000;
    private ProgressBar progressBar;
    private RelativeLayout loadingOverlay;
    private LinearLayout loadingContentContainer;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_HAS_SEEN_LOADING = "hasSeenLoading";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean hasSeenLoading = prefs.getBoolean(KEY_HAS_SEEN_LOADING, false); // Por defecto es false (no vista aún)

        if (hasSeenLoading) {
            // Si ya se vio la pantalla de carga, ocultar el overlay inmediatamente y asegurar que el contenido principal sea visible.
            findViewById(R.id.loading_overlay).setVisibility(View.GONE);
            findViewById(R.id.main_menu_content).setVisibility(View.VISIBLE); // Asegurar visibilidad del menú
        } else {
            // Es la primera vez que se carga o no se ha visto la pantalla de carga completa
            // 1. Encontrar las vistas de la capa de carga ---
            loadingOverlay = findViewById(R.id.loading_overlay);
            loadingContentContainer = findViewById(R.id.loading_content_container);
            progressBar = findViewById(R.id.progressBar);

            // 2. Mostrar la capa de carga inicialmente ---
            loadingOverlay.setVisibility(View.VISIBLE);
            loadingOverlay.setClickable(true);
            loadingOverlay.setFocusable(true);

            // -3. Iniciar la animación de carga secundaria ---
            iniciarCargaSecundaria();
        }

        // 4. Inicializar los botones del menú principal (estarán debajo del overlay o visibles directamente) ---
        Button btnPruebaHistoria = findViewById(R.id.btn_prueba_historia);
        Button btnNivelador = findViewById(R.id.btn_nivelador);
        Button btnDetectorMetales = findViewById(R.id.btn_detector_metales);

        btnPruebaHistoria.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            startActivity(intent);
        });

        btnNivelador.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AcelerometroActivity.class);
            startActivity(intent);
        });

        btnDetectorMetales.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MagnetometroActivity.class);
            startActivity(intent);
        });
    }

    // Método para iniciar la animación de la barra de progreso
    private void iniciarCargaSecundaria() {
        new CountDownTimer(SECONDARY_LOADING_DURATION, 30) {
            @Override
            public void onTick(long tiempoFin) {
                int progress = (int) (((SECONDARY_LOADING_DURATION - tiempoFin) / (double) SECONDARY_LOADING_DURATION) * 100);
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(100);
                iniciarFadeOutCargaSecundaria();

                // Al finalizar la carga, marcar que ya se vio la pantalla de carga
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_HAS_SEEN_LOADING, true); // Marcar como vista
                editor.apply();
            }
        }.start();
    }

    // Método para iniciar la animación de desvanecimiento de la capa de carga
    private void iniciarFadeOutCargaSecundaria() {
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        loadingContentContainer.startAnimation(fadeOut);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                loadingOverlay.setVisibility(View.GONE);
                // Asegurarse de que el contenido principal sea visible después de que la carga se oculte
                findViewById(R.id.main_menu_content).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
}