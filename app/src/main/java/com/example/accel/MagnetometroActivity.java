package com.example.accel;

import android.content.Intent;
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

    private static final String TAG = "MagnetometroActivity";
    private SensorManager sensorManager;
    private Sensor magnetometro;
    private TextView datosMagnetometro;
    private TextView estadoDeteccion;
    private ProgressBar progressBar;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private static final double UMBRAL_DETECCION_METAL = 75.0;
    private static final double UMBRAL_MAX_VISUAL = 150.0;
    private static final int BEEP_INTERVAL_MS = 200;
    private static final int VIBRATION_DURATION_MS = 100;

    private boolean isMetalDetected = false;
    private Handler handler;
    private Runnable beepRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetometro);

        // --- Configuración CONVENCIONAL de la Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar_magnetometro);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Manejo del botón de retroceso de la Toolbar con OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        // Inicializar vistas con IDs actualizados
        datosMagnetometro = findViewById(R.id.magnetometro_datos);
        estadoDeteccion = findViewById(R.id.estado_deteccion);
        progressBar = findViewById(R.id.magnetometro_progress_bar);

        sensorManager = getSystemService(SensorManager.class);
        vibrator = getSystemService(Vibrator.class);
        handler = new Handler(Looper.getMainLooper());

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.beep);
            if (mediaPlayer == null) {
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

        if (sensorManager != null) {
            magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            if (magnetometro == null) {
                Toast.makeText(this, "Magnetómetro no disponible en este dispositivo.", Toast.LENGTH_LONG).show();
                datosMagnetometro.setText("Magnetómetro no disponible.");
                estadoDeteccion.setText("N/A");
                progressBar.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "Servicio de sensores no disponible.", Toast.LENGTH_LONG).show();
            datosMagnetometro.setText("Servicio de sensores no disponible.");
            estadoDeteccion.setText("N/A");
            progressBar.setVisibility(View.GONE);
        }

        Button btnVolver = findViewById(R.id.boton_volver_magnetometro);
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(MagnetometroActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        beepRunnable = new Runnable() {
            @Override
            public void run() {
                if (isMetalDetected) {
                    reproducirSonidoVibracion();
                    handler.postDelayed(this, BEEP_INTERVAL_MS);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (magnetometro != null && sensorManager != null) {
            sensorManager.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        handler.removeCallbacks(beepRunnable);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double magnitud = Math.sqrt(x*x + y*y + z*z);

            String data = String.format(Locale.getDefault(), "Magnitud: %.2f μT", magnitud);
            datosMagnetometro.setText(data);

            int progress = (int) Math.min(magnitud, UMBRAL_MAX_VISUAL);
            progressBar.setProgress(progress);
            progressBar.setProgressTintList(ContextCompat.getColorStateList(this,
                    magnitud > UMBRAL_DETECCION_METAL ? android.R.color.holo_red_light : android.R.color.holo_green_light));


            if (magnitud > UMBRAL_DETECCION_METAL) {
                estadoDeteccion.setText("¡METAL DETECTADO! 🚨");
                estadoDeteccion.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
                if (!isMetalDetected) {
                    isMetalDetected = true;
                    handler.post(beepRunnable);
                }
            } else {
                estadoDeteccion.setText("Sin detección de metal ✅");
                estadoDeteccion.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
                if (isMetalDetected) {
                    isMetalDetected = false;
                    handler.removeCallbacks(beepRunnable);
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }
            }
        }
    }

    private void reproducirSonidoVibracion() {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }

        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION_MS, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}