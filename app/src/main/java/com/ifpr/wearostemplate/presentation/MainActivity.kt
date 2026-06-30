/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.ifpr.wearostemplate.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ifpr.wearostemplate.R

class MainActivity : AppCompatActivity() { // Alterado para AppCompatActivity para reconhecer os botões do XML tradicional
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContentView(R.layout.activity_main)

        // 1. Vincular os componentes do XML ajustado
        val btnPlay = findViewById<ImageButton>(R.id.btnPlay)
        val btnPerfil = findViewById<Button>(R.id.btnPerfil)

        // 2. Evento de clique para o botão Play (Iniciar Corrida)
        btnPlay.setOnClickListener {
            // Seu código para iniciar o treino entra aqui depois!
        }

        // 3. Evento de clique para abrir a PerfilActivity
        btnPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
    }
}