package com.example.accel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class QuizActivity extends AppCompatActivity {

    private TextView tvPreguntaNumero, tvPreguntaTexto;
    private RadioGroup radioGroup;
    private RadioButton rbOpcion1, rbOpcion2, rbOpcion3;
    private Button btnVerificar;

    private final String[] preguntas = {
            "¿Quién fue el primer europeo en ver el Océano Pacífico desde Panamá?",
            "¿Qué significan las estrellas en la bandera de Panamá?",
            "¿En qué año se inauguró el Canal de Panamá?",
            "¿Cuál es el ave nacional de Panamá?",
            "¿Qué es una 'pollera'?",
            "¿En qué año se separó Panamá de Colombia?"
    };

    private final String[][] opciones = {
            {"Cristóbal Colón", "Vasco Núñez de Balboa", "Hernán Cortés"},
            {"Pureza y honestidad", "La riqueza del país", "Las provincias"},
            {"1903", "1914", "1999"},
            {"El quetzal", "El loro", "El águila harpía"},
            {"Una comida típica", "Un vestido tradicional", "Un tipo de baile"},
            {"1903", "1821", "1925"}
    };

    private final String[] respuestasCorrectas = {
            "Vasco Núñez de Balboa",
            "Pureza y honestidad",
            "1914",
            "El águila harpía",
            "Un vestido tradicional",
            "1903"
    };

    private int preguntaActualIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Toolbar toolbar = findViewById(R.id.toolbar_quiz);
        setSupportActionBar(toolbar);

        // Habilita el botón de "volver atrás" (flecha) en la Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Oculta el título predeterminado
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });


        // Inicializar vistas del Quiz
        tvPreguntaNumero = findViewById(R.id.pregunta_numero);
        tvPreguntaTexto = findViewById(R.id.pregunta_texto);
        radioGroup = findViewById(R.id.opciones_radio_group);
        rbOpcion1 = findViewById(R.id.opcion1);
        rbOpcion2 = findViewById(R.id.opcion2);
        rbOpcion3 = findViewById(R.id.opcion3);
        btnVerificar = findViewById(R.id.boton_verificar);

        // Inicializar el botón de volver al menú
        Button btnVolverMenuQuiz = findViewById(R.id.btn_volver_menu_quiz);

        mostrarPregunta();

        btnVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarRespuesta();
            }
        });

        // Listener para el botón de volver al menú
        btnVolverMenuQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Este botón tiene un comportamiento específico para volver a MainActivity
                Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finaliza esta actividad
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Esto activará el OnBackPressedCallback que definimos en onCreate()
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarPregunta() {
        if (preguntaActualIndex < preguntas.length) {
            tvPreguntaNumero.setText("Pregunta " + (preguntaActualIndex + 1) + "/" + preguntas.length);
            tvPreguntaTexto.setText(preguntas[preguntaActualIndex]);
            rbOpcion1.setText(opciones[preguntaActualIndex][0]);
            rbOpcion2.setText(opciones[preguntaActualIndex][1]);
            rbOpcion3.setText(opciones[preguntaActualIndex][2]);
            radioGroup.clearCheck();
        } else {
            tvPreguntaTexto.setText("¡Felicidades, has completado la prueba!");
            radioGroup.setVisibility(View.GONE);
            btnVerificar.setText("Reiniciar prueba");
            btnVerificar.setOnClickListener(v -> {
                preguntaActualIndex = 0;
                radioGroup.setVisibility(View.VISIBLE);
                btnVerificar.setText("Verificar respuesta");
                mostrarPregunta();
            });
        }
    }

    private void verificarRespuesta() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Por favor, selecciona una opción", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton radioButtonSeleccionado = findViewById(selectedId);
        String respuestaSeleccionada = radioButtonSeleccionado.getText().toString();

        if (respuestaSeleccionada.equals(respuestasCorrectas[preguntaActualIndex])) {
            Toast.makeText(this, "¡Correcto! 🎉", Toast.LENGTH_SHORT).show();
            preguntaActualIndex++;
            mostrarPregunta();
        } else {
            Toast.makeText(this, "Respuesta incorrecta. ¡Inténtalo de nuevo!", Toast.LENGTH_SHORT).show();
        }
    }
}