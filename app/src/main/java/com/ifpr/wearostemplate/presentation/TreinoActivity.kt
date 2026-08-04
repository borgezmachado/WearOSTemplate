package com.ifpr.wearostemplate.presentation

import android.os.Bundle
import android.os.SystemClock
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.database.FirebaseDatabase
import com.ifpr.wearostemplate.R
import com.ifpr.wearostemplate.presentation.baseclasses.Corrida
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TreinoActivity : ComponentActivity() {

    private lateinit var chronometer: Chronometer
    private lateinit var btnStop: ImageButton
    private lateinit var txtData: TextView
    private lateinit var txtDistanciaTreino: TextView
    private lateinit var txtPaceTreino: TextView
    private lateinit var txtCaloriasTreino: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContentView(R.layout.activity_treino)

        chronometer = findViewById(R.id.chronometer)
        btnStop = findViewById(R.id.btnStop)
        txtData = findViewById(R.id.txtData)
        txtDistanciaTreino = findViewById(R.id.txtDistanciaTreino)
        txtPaceTreino = findViewById(R.id.txtPaceTreino)
        txtCaloriasTreino = findViewById(R.id.txtCaloriasTreino)

        // Define a data atual dinamicamente via variável em tempo de execução
        val sdfData = SimpleDateFormat("EEE, dd 'DE' MMM", Locale.getDefault())
        txtData.text = sdfData.format(Date()).uppercase()

        // Inicia o cronômetro
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()

        btnStop.setOnClickListener {
            salvarCorridaEFinalizar()
        }
    }

    private fun salvarCorridaEFinalizar() {
        chronometer.stop()

        val tempoSegundos = (SystemClock.elapsedRealtime() - chronometer.base) / 1000
        val distanciaKm = 2.5 // Substitua depois pelos dados reais do GPS/sensores

        val databaseRef = FirebaseDatabase.getInstance().getReference("corridas")
        val corridaId = databaseRef.push().key ?: return

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dataAtual = sdf.format(Date())

        val novaCorrida = Corrida(
            id = corridaId,
            distanciaKm = distanciaKm,
            tempoSegundos = tempoSegundos,
            data = dataAtual
        )

        databaseRef.child(corridaId).setValue(novaCorrida)
            .addOnSuccessListener {
                Toast.makeText(this, "Treino salvo com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Erro ao salvar: ${error.message}", Toast.LENGTH_LONG).show()
            }
    }
}