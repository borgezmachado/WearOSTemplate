/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.ifpr.wearostemplate.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
<<<<<<< HEAD
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
=======
import android.widget.Toast
import androidx.activity.ComponentActivity
>>>>>>> upstream/realtime-database
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.database.FirebaseDatabase
import com.ifpr.wearostemplate.R
import com.ifpr.wearostemplate.presentation.baseclasses.Corrida
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() { // Alterado para AppCompatActivity para reconhecer os botões do XML tradicional
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContentView(R.layout.activity_main)
<<<<<<< HEAD
=======

        val buttonPerfil = findViewById<Button>(R.id.btnPerfil)
        buttonPerfil.setOnClickListener{
            val intent = Intent(baseContext, PerfilActivity::class.java)
            startActivity(intent)
        }

        val btnStop = findViewById<Button>(R.id.btnStop)
        btnStop.setOnClickListener {
            val distanciaKm = 2.5
            val tempoSegundos = 900L
            salvarCorrida(distanciaKm, tempoSegundos)
            Toast.makeText(this, "Corrida salva!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun salvarCorrida(distanciaKm: Double, tempoSegundos:
    Long) {

    }


}
>>>>>>> upstream/realtime-database

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