package com.example.accel;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer; // Importar para la barra de progreso
import android.view.View; // Importar para View.GONE, View.VISIBLE
import android.view.animation.Animation; // Importar para animaciones
import android.view.animation.AnimationUtils; // Importar para cargar animaciones
import android.widget.Button;
import android.widget.LinearLayout; // Importar si usas LinearLayout para el contenido de carga
import android.widget.ProgressBar; // Importar para la ProgressBar
import android.widget.RelativeLayout; // Importar para la capa de carga
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Duración de la carga secundaria (después del splash nativo)
    private static final int SECONDARY_LOADING_DURATION = 3000; // 3 segundos
    private ProgressBar progressBar;
    private RelativeLayout loadingOverlay; // El contenedor completo de la capa de carga
    private LinearLayout loadingContentContainer; // El LinearLayout dentro del overlay (para la animación)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // MUY IMPORTANTE: super.onCreate() se encarga de que la pantalla de inicio nativa se muestre primero
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Carga tu layout principal

        // --- 1. Encontrar las vistas de la capa de carga ---
        loadingOverlay = findViewById(R.id.loading_overlay);
        loadingContentContainer = findViewById(R.id.loading_content_container); // El LinearLayout con el texto/imagen
        progressBar = findViewById(R.id.progressBar);

        // --- 2. Mostrar la capa de carga inicialmente ---
        // Esto la hace visible justo después de que MainActivity se carga
        loadingOverlay.setVisibility(View.VISIBLE);
        // Asegúrate de que capture los toques para evitar que el usuario interactúe con el menú principal antes de tiempo
        loadingOverlay.setClickable(true);
        loadingOverlay.setFocusable(true);

        // --- 3. Iniciar la animación de carga secundaria ---
        iniciarCargaSecundaria();

        // --- 4. Inicializar los botones del menú principal (estarán debajo del overlay) ---
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
        new CountDownTimer(SECONDARY_LOADING_DURATION, 30) { // Actualiza la barra cada 30ms
            @Override
            public void onTick(long tiempoFin) {
                // Calcula el progreso en un rango de 0 a 100
                int progress = (int) (((SECONDARY_LOADING_DURATION - tiempoFin) / (double) SECONDARY_LOADING_DURATION) * 100);
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                // Cuando el contador termina, la barra llega al 100%
                progressBar.setProgress(100);
                // Ahora, inicia la animación de salida para la capa de carga
                iniciarFadeOutCargaSecundaria();
            }
        }.start();
    }

    // Método para iniciar la animación de desvanecimiento de la capa de carga
    private void iniciarFadeOutCargaSecundaria() {
        // Carga la animación de fade_out (asegúrate de tener este archivo en res/anim/fade_out.xml)
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        // Aplica la animación al contenedor del contenido de carga
        loadingContentContainer.startAnimation(fadeOut);

        // Define qué hacer cuando la animación termine
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Cuando la animación de desvanecimiento termina, oculta completamente la capa de carga.
                // Esto revelará el contenido principal de MainActivity.
                loadingOverlay.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
}