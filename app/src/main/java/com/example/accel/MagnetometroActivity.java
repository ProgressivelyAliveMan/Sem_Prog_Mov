package com.example.accel;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import java.util.Locale;

public class MagnetometroActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MagnetometroActivity"; // Etiqueta para el logging
    private SensorManager sensorManager;
    private Sensor magnetometro;
    private TextView datosMagnetometro;
    private TextView estadoDeteccion;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private boolean isMetalDetected = false;

    private static final double UMBRAL_DETECCION_METAL = 75.0; // Ajustar este umbral

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetometro);

        datosMagnetometro = findViewById(R.id.magnetometer_datos);
        estadoDeteccion = findViewById(R.id.estado_deteccion);

        sensorManager = getSystemService(SensorManager.class);
        vibrator = getSystemService(Vibrator.class);

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.beep);
            if (mediaPlayer == null) {
                Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mediaPlayer = MediaPlayer.create(this, notificationUri);
                Toast.makeText(this, "Usando tono de notificación predeterminado para el detector.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Reemplazado e.printStackTrace() con Log.e()
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
            }
        } else {
            Toast.makeText(this, "Servicio de sensores no disponible.", Toast.LENGTH_LONG).show();
            datosMagnetometro.setText("Servicio de sensores no disponible.");
            estadoDeteccion.setText("N/A");
        }

        Button btnVolver = findViewById(R.id.boton_volver_magnetometro);
        btnVolver.setOnClickListener(v -> finish());
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double magnitud = Math.sqrt(x*x + y*y + z*z);

            // Se usa Locale.getDefault() para un formato consistente
            String data = String.format(Locale.getDefault(), "Eje X: %.2f μT\nEje Y: %.2f μT\nEje Z: %.2f μT\nMagnitud: %.2f μT", x, y, z, magnitud);
            datosMagnetometro.setText(data);

            if (magnitud > UMBRAL_DETECCION_METAL) {
                estadoDeteccion.setText("¡METAL DETECTADO! 🚨");
                estadoDeteccion.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
                if (!isMetalDetected) {
                    reproducirSonidoVibracion();
                    isMetalDetected = true;
                }
            } else {
                estadoDeteccion.setText("Sin detección de metal ✅");
                estadoDeteccion.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
                isMetalDetected = false;
            }
        }
    }

    private void reproducirSonidoVibracion() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.seekTo(0);
            }
            mediaPlayer.start();
        }

        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}