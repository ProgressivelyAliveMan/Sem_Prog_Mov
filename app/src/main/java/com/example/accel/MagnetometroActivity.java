package com.example.accel;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class MagnetometroActivity extends AppCompatActivity implements SensorEventListener {

    // Variables para gestionar el sensor, UI y efectos (sonido/vibración).
    private static final String TAG = "MagnetometroActivity"; // Para mensajes de log.
    private SensorManager sensorManager;
    private Sensor magnetometro;
    private TextView datosMagnetometro;
    private TextView estadoDeteccion;
    private ProgressBar progressBar;
    private MediaPlayer mediaPlayer; // Para reproducir sonidos.
    private Vibrator vibrator; // Para vibrar el dispositivo.

    // Constantes para la lógica de detección y efectos.
    private static final double UMBRAL_DETECCION_METAL = 75.0; // Magnitud para considerar "metal".
    private static final double UMBRAL_MAX_VISUAL = 150.0; // Máximo en ProgressBar.
    private static final int BEEP_INTERVAL_MS = 200; // Intervalo de sonido al detectar.
    private static final int VIBRATION_DURATION_MS = 100; // Duración de la vibración.

    private boolean isMetalDetected = false; // Estado de detección.
    private Handler handler; // Para programar tareas (beeps).
    private Runnable beepRunnable; // Tarea para el sonido repetitivo.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetometro); // Infla el layout.

        // Configuración de la Toolbar (barra superior).
        Toolbar toolbar = findViewById(R.id.toolbar_magnetometro);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Habilita botón de volver.
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Oculta título predeterminado.
        }

        // Manejo del botón de retroceso de la Toolbar.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Cierra la Activity actual.
            }
        });

        // Inicializar vistas de la UI.
        datosMagnetometro = findViewById(R.id.magnetometro_datos);
        estadoDeteccion = findViewById(R.id.estado_deteccion);
        progressBar = findViewById(R.id.magnetometro_progress_bar);

        // Obtiene servicios del sistema.
        sensorManager = getSystemService(SensorManager.class);
        vibrator = getSystemService(Vibrator.class);
        handler = new Handler(Looper.getMainLooper()); // Handler para el hilo principal.

        // Intenta cargar el sonido "beep.mp3" o usa el tono de notificación predeterminado.
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.beep);
            if (mediaPlayer == null) {
                // Fallback a tono de notificación si beep.mp3 no se encuentra.
                Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mediaPlayer = MediaPlayer.create(this, notificationUri);
                Toast.makeText(this, "Usando tono de notificación predeterminado para el detector.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar beep.mp3, usando tono predeterminado.", e);
            Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mediaPlayer = MediaPlayer.create(this, notificationUri);
            Toast.makeText(this, "Error al cargar beep.mp3, usando tono predeterminado.", Toast.LENGTH_LONG).show();
        }

        // Verifica la disponibilidad del magnetómetro.
        if (sensorManager != null) {
            magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            if (magnetometro == null) {
                // Muestra mensaje si el sensor no está disponible y oculta UI.
                Toast.makeText(this, "Magnetómetro no disponible en este dispositivo.", Toast.LENGTH_LONG).show();
                datosMagnetometro.setText("Magnetómetro no disponible.");
                estadoDeteccion.setText("N/A");
                progressBar.setVisibility(View.GONE);
            }
        } else {
            // Muestra mensaje si el servicio de sensores no está disponible.
            Toast.makeText(this, "Servicio de sensores no disponible.", Toast.LENGTH_LONG).show();
            datosMagnetometro.setText("Servicio de sensores no disponible.");
            estadoDeteccion.setText("N/A");
            progressBar.setVisibility(View.GONE);
        }

        // Configura el botón para volver al menú principal.
        Button btnVolver = findViewById(R.id.boton_volver_magnetometro);
        btnVolver.setOnClickListener(v -> {
            finish();
        });

        // Define la tarea repetitiva para reproducir sonido y vibración.
        beepRunnable = new Runnable() {
            @Override
            public void run() {
                if (isMetalDetected) {
                    reproducirSonidoVibracion(); // Llama a la función de sonido/vibración.
                    handler.postDelayed(this, BEEP_INTERVAL_MS); // Repite la tarea.
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registra el listener del magnetómetro cuando la Activity está activa.
        if (magnetometro != null && sensorManager != null) {
            sensorManager.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistra el listener del sensor para ahorrar batería.
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        handler.removeCallbacks(beepRunnable); // Detiene las repeticiones del beep.
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop(); // Detiene el sonido si está reproduciéndose.
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libera recursos del MediaPlayer y Handler al destruir la Activity.
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null); // Elimina cualquier tarea pendiente.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Se llama cada vez que los valores del sensor cambian.
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float x = event.values[0]; // Componente X del campo magnético.
            float y = event.values[1]; // Componente Y del campo magnético.
            float z = event.values[2]; // Componente Z del campo magnético.

            // Calcula la magnitud total del campo magnético.
            double magnitud = Math.sqrt(x*x + y*y + z*z);

            // Muestra la magnitud en la UI.
            String data = String.format(Locale.getDefault(), "Magnitud: %.2f μT", magnitud);
            datosMagnetometro.setText(data);

            // Actualiza la ProgressBar y su color según la magnitud.
            int progress = (int) Math.min(magnitud, UMBRAL_MAX_VISUAL);
            progressBar.setProgress(progress);
            progressBar.setProgressTintList(ContextCompat.getColorStateList(this,
                    magnitud > UMBRAL_DETECCION_METAL ? android.R.color.holo_red_light : android.R.color.holo_green_light));

            // Lógica de detección de metal y control de sonido/vibración.
            if (magnitud > UMBRAL_DETECCION_METAL) {
                estadoDeteccion.setText("¡METAL DETECTADO! 🚨");
                estadoDeteccion.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
                if (!isMetalDetected) {
                    isMetalDetected = true;
                    handler.post(beepRunnable); // Inicia la repetición del beep.
                }
            } else {
                estadoDeteccion.setText("Sin detección de metal ✅");
                estadoDeteccion.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
                if (isMetalDetected) {
                    isMetalDetected = false;
                    handler.removeCallbacks(beepRunnable); // Detiene la repetición del beep.
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop(); // Asegura que el sonido se detenga.
                    }
                }
            }
        }
    }

    // Función para reproducir el sonido y la vibración.
    private void reproducirSonidoVibracion() {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(0); // Reinicia el sonido al principio.
            mediaPlayer.start();   // Inicia la reproducción.
        }
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION_MS, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Se llama si la precisión del sensor cambia (no se usa en este caso).
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Maneja el clic en el botón de retroceso de la Toolbar.
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed(); // Simula la pulsación del botón de retroceso.
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}