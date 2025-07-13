package com.example.accel; // Asegúrate de que este sea tu paquete

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imagenBandera = findViewById(R.id.imagen_bandera);
        ImageView imagenLogoUtp = findViewById(R.id.imagen_logo_utp);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        imagenBandera.startAnimation(fadeIn);
        imagenLogoUtp.startAnimation(fadeIn);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);
    }
}