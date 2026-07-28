package com.ifpr.wearostemplate.presentation

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ifpr.wearostemplate.R
import java.util.Locale

class PerfilActivity : ComponentActivity() {

    private lateinit var txtNome: TextView
    private lateinit var txtDistanciaTotal: TextView
    private lateinit var txtPaceMedio: TextView
    private lateinit var btnVoltar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        txtNome = findViewById(R.id.txtNome)
        txtDistanciaTotal = findViewById(R.id.txtDistanciaTotal)
        txtPaceMedio = findViewById(R.id.txtPaceMedio)
        btnVoltar = findViewById(R.id.btnVoltar)

        txtNome.text = "PILOTO XLR8"

        btnVoltar.setOnClickListener {
            finish()
        }

        carregarEstatisticasRealtimeDatabase()
    }

    private fun carregarEstatisticasRealtimeDatabase() {
        // Conecta no nó "corridas" do Realtime Database
        val ref = FirebaseDatabase.getInstance().getReference("corridas")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var somaDistanciaKm = 0.0
                var somaTempoSegundos = 0L

                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        // Leitura direta e segura das propriedades do Realtime Database
                        val distancia = child.child("distanciaKm").getValue(Double::class.java)
                            ?: child.child("distanciaKm").getValue(Long::class.java)?.toDouble()
                            ?: 0.0

                        val tempo = child.child("tempoSegundos").getValue(Long::class.java)
                            ?: child.child("tempoSegundos").getValue(Int::class.java)?.toLong()
                            ?: 0L

                        somaDistanciaKm += distancia
                        somaTempoSegundos += tempo
                    }
                }

                atualizarInterface(somaDistanciaKm, somaTempoSegundos)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@PerfilActivity,
                    "Erro Realtime DB: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun atualizarInterface(distanciaTotalKm: Double, tempoTotalSegundos: Long) {
        // Exibe distância total (ex: 145.8 KM)
        txtDistanciaTotal.text = String.format(Locale.US, "%.1f KM", distanciaTotalKm)

        // Calcula Pace Médio (Tempo total em minutos / Distância total em KM)
        if (distanciaTotalKm > 0) {
            val tempoMinutos = tempoTotalSegundos / 60.0
            val pace = tempoMinutos / distanciaTotalKm
            val minutos = pace.toInt()
            val segundos = ((pace - minutos) * 60).toInt()

            txtPaceMedio.text = String.format(Locale.US, "%02d:%02d /KM", minutos, segundos)
        } else {
            txtPaceMedio.text = "00:00 /KM"
        }
    }
}