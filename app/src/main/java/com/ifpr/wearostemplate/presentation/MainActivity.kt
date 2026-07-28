package com.ifpr.wearostemplate.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.database.FirebaseDatabase
import com.ifpr.wearostemplate.R
import com.ifpr.wearostemplate.presentation.baseclasses.Corrida
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContentView(R.layout.activity_main)

        // 1. Vincular os componentes do XML
        val btnPlay = findViewById<ImageButton>(R.id.btnPlay)
        val btnPerfil = findViewById<Button>(R.id.btnPerfil)

        // 2. Evento de clique para salvar uma corrida ao apertar Play
        btnPlay.setOnClickListener {
            // Valores de teste para gravar no banco
            val distanciaKm = 2.5
            val tempoSegundos = 900L

            salvarCorrida(distanciaKm, tempoSegundos)
        }

        // 3. Evento de clique para abrir a PerfilActivity
        btnPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
    }

    private fun salvarCorrida(distanciaKm: Double, tempoSegundos: Long) {
        // Pega a referência do nó "corridas" no Realtime Database
        val databaseRef = FirebaseDatabase.getInstance().getReference("corridas")

        // Gera uma chave/ID única no Firebase
        val corridaId = databaseRef.push().key ?: return

        // Pega a data e hora atual formatada
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dataAtual = sdf.format(Date())

        // Cria o objeto Corrida
        val novaCorrida = Corrida(
            id = corridaId,
            distanciaKm = distanciaKm,
            tempoSegundos = tempoSegundos,
            data = dataAtual
        )

        // Salva os dados no banco de dados
        databaseRef.child(corridaId).setValue(novaCorrida)
            .addOnSuccessListener {
                Toast.makeText(this, "Corrida salva com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Erro ao salvar: ${error.message}", Toast.LENGTH_LONG).show()
            }
    }
}