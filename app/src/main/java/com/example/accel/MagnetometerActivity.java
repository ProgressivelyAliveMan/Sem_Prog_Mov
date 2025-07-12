package com.example.accel; // Asegúrate de que este sea tu paquete

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MagnetometerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetometer);

        Button btnVolver = findViewById(R.id.boton_volver_magnetometro);
        btnVolver.setOnClickListener(v -> finish()); // Cierra esta actividad
    }
}
