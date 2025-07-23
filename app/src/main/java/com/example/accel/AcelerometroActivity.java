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

    private SensorManager sensorManager;
    private Sensor acelerometro;
    private TextView acelerometroDatos;
    private ConstraintLayout mainLayout;
    private ConstraintLayout burbujaNivelLayout;
    private ImageView burbujaNivel;
    private TextView textoNivelado;

    private static final float GRAVEDAD_IDEAL = 9.81f;
    private static final float TOLERANCIA_GRAVEDAD = 0.15f;
    private static final float TOLERANCIA_INCLINACION = 0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometro);

        Toolbar toolbar = findViewById(R.id.toolbar_acelerometro); // ID correcto
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        acelerometroDatos = findViewById(R.id.acelerometro_datos);
        mainLayout = findViewById(R.id.main_layout);
        burbujaNivelLayout = findViewById(R.id.burbuja_nivel_layout); // ID correcto
        burbujaNivel = findViewById(R.id.burbuja_nivel); // ID correcto
        textoNivelado = findViewById(R.id.texto_nivelado); // ID correcto

        Button btnVolver = findViewById(R.id.boton_volver_acelerometro); // ID correcto

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (acelerometro == null) {
                Toast.makeText(this, "Acelerómetro no disponible en este dispositivo.", Toast.LENGTH_LONG).show();
                acelerometroDatos.setText("Acelerómetro no disponible.");
                burbujaNivelLayout.setVisibility(View.GONE);
                textoNivelado.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "Servicio de sensores no disponible.", Toast.LENGTH_LONG).show();
            acelerometroDatos.setText("Servicio de sensores no disponible.");
            burbujaNivelLayout.setVisibility(View.GONE);
            textoNivelado.setVisibility(View.GONE);
        }

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME);
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
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            String data = String.format(Locale.getDefault(), "X: %.2f m/s²\nY: %.2f m/s²\nZ: %.2f m/s² (Gravedad)", x, y, z);
            acelerometroDatos.setText(data);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(burbujaNivelLayout);

            float maxDeviation = 5.0f;
            float biasX = 0.5f - (x / (maxDeviation * 2));
            float biasY = 0.5f + (y / (maxDeviation * 2));

            biasX = Math.max(0f, Math.min(1f, biasX));
            biasY = Math.max(0f, Math.min(1f, biasY));

            constraintSet.setHorizontalBias(burbujaNivel.getId(), biasX);
            constraintSet.setVerticalBias(burbujaNivel.getId(), biasY);
            constraintSet.applyTo(burbujaNivelLayout);

            boolean isZLevel = Math.abs(z - GRAVEDAD_IDEAL) < TOLERANCIA_GRAVEDAD;
            boolean isXYLevel = Math.abs(x) < TOLERANCIA_INCLINACION && Math.abs(y) < TOLERANCIA_INCLINACION;

            if (isZLevel && isXYLevel) {
                mainLayout.setBackgroundColor(Color.parseColor("#E8F5E9"));
                burbujaNivelLayout.setBackgroundColor(Color.parseColor("#A5D6A7"));
                textoNivelado.setText("¡NIVELADO (BOCA ARRIBA)! ✅");
                textoNivelado.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            } else if (isXYLevel) {
                mainLayout.setBackgroundColor(Color.parseColor("#FFFDE7"));
                burbujaNivelLayout.setBackgroundColor(Color.parseColor("#FFCC80"));
                textoNivelado.setText("¡NIVELADO (BOCA ABAJO)! ✅");
                textoNivelado.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
            } else {
                mainLayout.setBackgroundColor(Color.WHITE);
                burbujaNivelLayout.setBackgroundColor(Color.parseColor("#F0F0F0"));
                textoNivelado.setText("No nivelado");
                textoNivelado.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            }
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