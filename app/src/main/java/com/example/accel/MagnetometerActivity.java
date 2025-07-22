package com.example.accel;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MagnetometerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magnetometro;
    private TextView datosMagnetometro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetometer);

        datosMagnetometro = findViewById(R.id.magnetometer_datos);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            if (magnetometro == null) {
                Toast.makeText(this, "Magnetómetro no disponible en este dispositivo.", Toast.LENGTH_LONG).show();
                datosMagnetometro.setText("Magnetómetro no disponible.");
            }
        } else {
            Toast.makeText(this, "Servicio de sensores no disponible.", Toast.LENGTH_LONG).show();
            datosMagnetometro.setText("Servicio de sensores no disponible.");
        }

        Button btnVolver = findViewById(R.id.boton_volver_magnetometro);
        btnVolver.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (magnetometro != null) {
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
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double magnitud = Math.sqrt(x*x + y*y + z*z);

            String data = String.format("X: %.2f μT\nY: %.2f μT\nZ: %.2f μT\nMagnitud: %.2f μT", x, y, z, magnitud);
            datosMagnetometro.setText(data);

            // Opcional: Lógica simple para una "detección" visual o sonora de metales
            if (magnitud > 100) { // Este umbral es un ejemplo, ajústalo según tus pruebas
                // Puedes cambiar el color de fondo, mostrar un ícono, reproducir un sonido.
                // Por ejemplo:
                // findViewById(R.id.main).setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                // O Toast.makeText(this, "¡Metal detectado!", Toast.LENGTH_SHORT).show();
            } else {
                // findViewById(R.id.main).setBackgroundColor(getResources().getColor(android.R.color.white));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}