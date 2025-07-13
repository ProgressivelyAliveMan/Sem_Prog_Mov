package com.example.accel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {

    private TextView tvPreguntaNumero, tvPreguntaTexto;
    private RadioGroup radioGroup;
    private RadioButton rbOpcion1, rbOpcion2, rbOpcion3;
    private Button btnVerificar;
    // Eliminamos btnSiguientePantalla porque la navegación a los sensores
    // se gestionará desde el MenuActivity.
    // private Button btnSiguientePantalla;

    private Button btnVolverMenuQuiz; // Declaramos el nuevo botón para volver al menú

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
            {"Pureza y Honestidad", "La riqueza del país", "Las provincias"},
            {"1903", "1914", "1999"},
            {"El Quetzal", "El Loro", "El Águila Harpía"},
            {"Una comida típica", "Un vestido tradicional", "Un tipo de baile"},
            {"1903", "1821", "1925"}
    };

    private final String[] respuestasCorrectas = {
            "Vasco Núñez de Balboa",
            "Pureza y Honestidad",
            "1914",
            "El Águila Harpía",
            "Un vestido tradicional",
            "1903"
    };

    private int preguntaActualIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Inicializar vistas
        tvPreguntaNumero = findViewById(R.id.pregunta_numero);
        tvPreguntaTexto = findViewById(R.id.pregunta_texto);
        radioGroup = findViewById(R.id.opciones_radio_group);
        rbOpcion1 = findViewById(R.id.opcion1);
        rbOpcion2 = findViewById(R.id.opcion2);
        rbOpcion3 = findViewById(R.id.opcion3);
        btnVerificar = findViewById(R.id.boton_verificar);
        // Eliminamos la inicialización de btnSiguientePantalla
        // btnSiguientePantalla = findViewById(R.id.boton_siguiente_pantalla);

        // Inicializar el nuevo botón de volver al menú
        btnVolverMenuQuiz = findViewById(R.id.btn_volver_menu_quiz); // Asegúrate de que este ID coincida con tu XML

        mostrarPregunta();

        btnVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarRespuesta();
            }
        });

        // Configurar el Listener para el botón de volver al menú
        btnVolverMenuQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Esto cierra QuizActivity y regresa a la actividad anterior (MenuActivity)
            }
        });
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
            // Fin del quiz
            tvPreguntaTexto.setText("¡Felicidades, has completado la prueba!");
            radioGroup.setVisibility(View.GONE);
            btnVerificar.setText("Reiniciar Prueba");
            btnVerificar.setOnClickListener(v -> {
                preguntaActualIndex = 0;
                radioGroup.setVisibility(View.VISIBLE);
                btnVerificar.setText("Verificar Respuesta");
                mostrarPregunta();
            });
            // Opcional: Si quieres ocultar el botón de volver cuando el quiz está "terminado" y se muestra el reiniciar
            // btnVolverMenuQuiz.setVisibility(View.GONE); // Puedes añadir esta línea si lo deseas
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