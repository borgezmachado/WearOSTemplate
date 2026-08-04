package com.ifpr.wearostemplate.presentation

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ifpr.wearostemplate.R
import com.ifpr.wearostemplate.presentation.baseclasses.Corrida
import java.util.Locale

class PerfilActivity : ComponentActivity() {

    private lateinit var txtDistanciaTotal: TextView
    private lateinit var txtTempoTotal: TextView
    private lateinit var txtPaceMedio: TextView
    private lateinit var txtNome: TextView
    private lateinit var imgFotoPerfil: ImageView
    private lateinit var btnVoltar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContentView(R.layout.activity_perfil)

        txtDistanciaTotal = findViewById(R.id.txtDistanciaTotal)
        txtTempoTotal = findViewById(R.id.txtTempoTotal)
        txtPaceMedio = findViewById(R.id.txtPaceMedio)
        txtNome = findViewById(R.id.txtNome)
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil)
        btnVoltar = findViewById(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            finish()
        }

        carregarDadosDoFirebase()
    }

    private fun carregarDadosDoFirebase() {
        val databaseRef = FirebaseDatabase.getInstance().getReference("corridas")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var distanciaTotalKm = 0.0
                var tempoTotalSegundos = 0L

                for (item in snapshot.children) {
                    val corrida = item.getValue(Corrida::class.java)
                    if (corrida != null) {
                        distanciaTotalKm += corrida.distanciaKm
                        tempoTotalSegundos += corrida.tempoSegundos
                    }
                }

                atualizarInterface(distanciaTotalKm, tempoTotalSegundos)
            }

            override fun onCancelled(error: DatabaseError) {
                // Em caso de falha de conexão, exibe dados padrão zerados
                atualizarInterface(0.0, 0L)
            }
        })
    }

    private fun atualizarInterface(distanciaKm: Double, tempoSegundos: Long) {
        // 1. Formata Distância
        txtDistanciaTotal.text = String.format(Locale.US, "%.1f KM", distanciaKm)

        // 2. Formata Tempo Total em Horas e Minutos (Garante exibição até para 0s)
        val horas = tempoSegundos / 3600
        val minutos = (tempoSegundos % 3600) / 60
        val segundosRestantes = tempoSegundos % 60

        txtTempoTotal.text = if (horas > 0) {
            String.format(Locale.US, "%02dh %02dm", horas, minutos)
        } else {
            String.format(Locale.US, "%02dm %02ds", minutos, segundosRestantes)
        }

        // 3. Calcula o Pace Médio (min/km)
        if (distanciaKm > 0.0 && tempoSegundos > 0L) {
            val tempoTotalMinutos = tempoSegundos / 60.0
            val paceMinutosPorKm = tempoTotalMinutos / distanciaKm

            val paceMin = paceMinutosPorKm.toInt()
            val paceSeg = ((paceMinutosPorKm - paceMin) * 60).toInt()

            txtPaceMedio.text = String.format(Locale.US, "%02d:%02d /KM", paceMin, paceSeg)
        } else {
            txtPaceMedio.text = "--:-- /KM"
        }
    }
}