package com.example.accel;

import android.os.Bundle;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Button;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor acelerometro;
    private TextView acelerometroDatos;

    private Button btnVolver, btnSiguiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accelerometer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        acelerometroDatos = findViewById(R.id.acelerometro_datos);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Obtener el sensor de acelerómetro
        if (sensorManager != null) {
            acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            // Verificar si el acelerómetro existe en el dispositivo
            if (acelerometro == null) {
                Toast.makeText(this, "Acelerómetro no disponible en este dispositivo.", Toast.LENGTH_LONG).show();
                acelerometroDatos.setText("Acelerómetro no disponible.");
            }
        } else {
            Toast.makeText(this, "Servicio de sensores no disponible.", Toast.LENGTH_LONG).show();
            acelerometroDatos.setText("Servicio de sensores no disponible.");
        }

        btnVolver = findViewById(R.id.boton_volver);
        btnSiguiente = findViewById(R.id.boton_siguiente);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra la actividad actual y vuelve a la anterior (QuizActivity)
                finish();
            }
        });

        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia la actividad del magnetómetro
                Intent intent = new Intent(AccelerometerActivity.this, MagnetometerActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Registrar el listener del sensor cuando la actividad esté activa
        if (acelerometro != null) {
            // SENSOR_DELAY_NORMAL es un retardo común, se puede usar FASTEST para más velocidad
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desactivar el listener cuando la actividad no esté en primer plano para ahorrar batería
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Este método se llama cuando los datos del sensor cambian
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0]; // Aceleración en el eje X
            float y = event.values[1]; // Aceleración en el eje Y
            float z = event.values[2]; // Aceleración en el eje Z

            String data = String.format("X: %.2f\nY: %.2f\nZ: %.2f", x, y, z);
            acelerometroDatos.setText(data);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}