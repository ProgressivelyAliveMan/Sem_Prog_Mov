package com.example.accel;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor acelerometro;
    private TextView acelerometroDatos;
    private Button btnVolver;
    private ConstraintLayout mainLayout; // Variable para el layout principal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        // Inicializar vistas
        acelerometroDatos = findViewById(R.id.acelerometro_datos);
        mainLayout = findViewById(R.id.main_layout); // Conectar con el layout
        btnVolver = findViewById(R.id.boton_volver);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Obtener el sensor de acelerómetro
        if (sensorManager != null) {
            acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (acelerometro == null) {
                Toast.makeText(this, "Acelerómetro no disponible en este dispositivo.", Toast.LENGTH_LONG).show();
                acelerometroDatos.setText("Acelerómetro no disponible.");
            }
        } else {
            Toast.makeText(this, "Servicio de sensores no disponible.", Toast.LENGTH_LONG).show();
        }

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la actividad y vuelve a la anterior
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
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
            float z = event.values[2]; // Este valor representa la gravedad en el eje Z

            String data = String.format("X: %.2f\nY: %.2f\nGravedad: %.2f", x, y, z);
            acelerometroDatos.setText(data);

            // Comprueba si el valor de Z (gravedad) está dentro del rango deseado.
            if (z >= 9.79 && z <= 9.86) {
                // Si está nivelado, cambia el fondo a un verde suave.
                mainLayout.setBackgroundColor(Color.parseColor("#C8E6C9"));
            } else {
                // Si no está nivelado, vuelve al color de fondo por defecto (blanco).
                mainLayout.setBackgroundColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
