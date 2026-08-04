package com.ifpr.wearostemplate.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ifpr.wearostemplate.R

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContentView(R.layout.activity_main)

        // 1. Vincular os componentes do XML com os IDs corretos
        val btnPlay = findViewById<ImageButton>(R.id.btnPlay)
        val btnPerfil = findViewById<Button>(R.id.btnPerfil)

        // 2. Evento de clique para iniciar o treino (abre a TreinoActivity)
        btnPlay.setOnClickListener {
            val intent = Intent(this, TreinoActivity::class.java)
            startActivity(intent)
        }

        // 3. Evento de clique para abrir a PerfilActivity
        btnPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
    }
}   