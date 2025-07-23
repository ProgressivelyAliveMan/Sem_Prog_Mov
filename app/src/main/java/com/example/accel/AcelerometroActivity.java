package com.example.accel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class AcelerometroActivity extends AppCompatActivity implements SensorEventListener {

    // Variables para gestionar el sensor y la UI.
    private SensorManager sensorManager;
    private Sensor acelerometro;
    private TextView acelerometroDatos;
    private ConstraintLayout mainLayout;
    private ConstraintLayout burbujaNivelLayout;
    private ImageView burbujaNivel;
    private TextView textoNivelado;

    // Constantes para la lógica de nivelación.
    private static final float GRAVEDAD_IDEAL = 9.81f;
    private static final float TOLERANCIA_GRAVEDAD = 0.15f;
    private static final float TOLERANCIA_INCLINACION = 0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometro); // Infla el layout de la Activity.

        // Configuración de la Toolbar (barra superior).
        Toolbar toolbar = findViewById(R.id.toolbar_acelerometro);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Habilita el botón de "volver".
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Oculta el título predeterminado.
        }

        // Manejo del botón de retroceso físico/virtual.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Cierra la Activity actual.
            }
        });

        // Inicialización de vistas de la UI.
        acelerometroDatos = findViewById(R.id.acelerometro_datos);
        mainLayout = findViewById(R.id.main_layout);
        burbujaNivelLayout = findViewById(R.id.burbuja_nivel_layout);
        burbujaNivel = findViewById(R.id.burbuja_nivel);
        textoNivelado = findViewById(R.id.texto_nivelado);

        Button btnVolver = findViewById(R.id.boton_volver_acelerometro); // Botón para volver al menú principal.

        // Obtiene el SensorManager del sistema.
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Verifica si el acelerómetro está disponible.
        if (sensorManager != null) {
            acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (acelerometro == null) {
                // Muestra un mensaje si el sensor no existe y oculta elementos de UI.
                Toast.makeText(this, "Acelerómetro no disponible en este dispositivo.", Toast.LENGTH_LONG).show();
                acelerometroDatos.setText("Acelerómetro no disponible.");
                burbujaNivelLayout.setVisibility(View.GONE);
                textoNivelado.setVisibility(View.GONE);
            }
        } else {
            // Muestra un mensaje si el servicio de sensores no está disponible.
            Toast.makeText(this, "Servicio de sensores no disponible.", Toast.LENGTH_LONG).show();
            acelerometroDatos.setText("Servicio de sensores no disponible.");
            burbujaNivelLayout.setVisibility(View.GONE);
            textoNivelado.setVisibility(View.GONE);
        }

        // Configura el listener para el botón de volver.
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega de vuelta a MainActivity, limpiando el historial de actividades.
                Intent intent = new Intent(AcelerometroActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registra el listener del sensor cuando la Activity está activa.
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistra el listener del sensor cuando la Activity no está en primer plano para ahorrar batería.
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Se llama cada vez que los valores del sensor cambian.
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0]; // Aceleración en eje X.
            float y = event.values[1]; // Aceleración en eje Y.
            float z = event.values[2]; // Aceleración en eje Z (gravedad).

            // Muestra los datos del acelerómetro en un TextView.
            String data = String.format(Locale.getDefault(), "X: %.2f m/s²\nY: %.2f m/s²\nZ: %.2f m/s² (Gravedad)", x, y, z);
            acelerometroDatos.setText(data);

            // Mueve la burbuja en la UI según la inclinación.
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(burbujaNivelLayout);
            float maxDeviation = 5.0f; // Máxima desviación para la burbuja.
            float biasX = 0.5f - (x / (maxDeviation * 2)); // Calcula el sesgo X.
            float biasY = 0.5f + (y / (maxDeviation * 2)); // Calcula el sesgo Y.
            biasX = Math.max(0f, Math.min(1f, biasX)); // Asegura que el sesgo esté entre 0 y 1.
            biasY = Math.max(0f, Math.min(1f, biasY));
            constraintSet.setHorizontalBias(burbujaNivel.getId(), biasX);
            constraintSet.setVerticalBias(burbujaNivel.getId(), biasY);
            constraintSet.applyTo(burbujaNivelLayout); // Aplica los cambios a la burbuja.

            // Lógica para determinar el estado de nivelación y actualizar la UI.
            boolean isZLevel = Math.abs(z - GRAVEDAD_IDEAL) < TOLERANCIA_GRAVEDAD; // ¿Eje Z cercano a la gravedad? (Boca arriba)
            boolean isXYLevel = Math.abs(x) < TOLERANCIA_INCLINACION && Math.abs(y) < TOLERANCIA_INCLINACION; // ¿Ejes X e Y cercanos a cero?

            if (isZLevel && isXYLevel) {
                // Estado: Nivelado y boca arriba.
                mainLayout.setBackgroundColor(Color.parseColor("#E8F5E9")); // Fondo verde claro.
                burbujaNivelLayout.setBackgroundColor(Color.parseColor("#A5D6A7")); // Fondo burbuja verde.
                textoNivelado.setText("¡NIVELADO (BOCA ARRIBA)! ✅");
                textoNivelado.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            } else if (isXYLevel) {
                // Estado: Nivelado (posiblemente boca abajo o sin alineación con gravedad).
                mainLayout.setBackgroundColor(Color.parseColor("#FFFDE7")); // Fondo amarillo claro.
                burbujaNivelLayout.setBackgroundColor(Color.parseColor("#FFCC80")); // Fondo burbuja naranja.
                textoNivelado.setText("¡NIVELADO (BOCA ABAJO)! ✅"); // Asume que si XY es plano, está boca abajo si Z no es gravedad.
                textoNivelado.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
            } else {
                // Estado: No nivelado.
                mainLayout.setBackgroundColor(Color.WHITE); // Fondo blanco.
                burbujaNivelLayout.setBackgroundColor(Color.parseColor("#F0F0F0")); // Fondo burbuja gris.
                textoNivelado.setText("No nivelado");
                textoNivelado.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Se llama si la precisión del sensor cambia (normalmente no se usa para acelerómetro).
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