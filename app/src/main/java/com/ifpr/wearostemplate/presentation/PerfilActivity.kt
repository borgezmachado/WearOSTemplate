package com.ifpr.wearostemplate.presentation

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.BlurMaskFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
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

    private lateinit var txtDistanciaTotal: TextView
    private lateinit var txtTempoTotal: TextView
    private lateinit var txtPaceMedio: TextView
    private lateinit var txtNome: TextView
    private lateinit var imgFotoPerfil: ImageView
    private lateinit var btnVoltar: ImageButton
    private lateinit var cardStats: View

    private var animadorPulso: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContentView(R.layout.activity_perfil)

        // 1. Mapeamento das views do layout XML
        txtDistanciaTotal = findViewById(R.id.txtDistanciaTotal)
        txtTempoTotal = findViewById(R.id.txtTempoTotal)
        txtPaceMedio = findViewById(R.id.txtPaceMedio)
        txtNome = findViewById(R.id.txtNome)
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil)
        btnVoltar = findViewById(R.id.btnVoltar)
        cardStats = findViewById(R.id.cardStats)

        // 2. Aplicação de efeito visual
        aplicarEfeitoGlowNeon(txtNome)

        // 3. Execução das animações de forma segura
        window.decorView.post {
            executarAnimacoesEntrada()
            iniciarAnimacaoPulso(imgFotoPerfil)
        }

        // 4. Ação do botão voltar
        btnVoltar.setOnClickListener { view ->
            view.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(90)
                .withEndAction {
                    view.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(90)
                        .withEndAction { finish() }
                        .start()
                }.start()
        }

        // 5. Carregamento e vinculação dos dados do Firebase
        carregarDadosDoFirebase()
    }

    private fun aplicarEfeitoGlowNeon(textView: TextView) {
        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        textView.paint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.SOLID)
    }

    private fun executarAnimacoesEntrada() {
        val elementos = listOf(imgFotoPerfil, txtNome, cardStats, btnVoltar)

        elementos.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 30f

            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(350)
                .setStartDelay((index * 70).toLong())
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    private fun iniciarAnimacaoPulso(view: View) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.96f, 1.04f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.96f, 1.04f)

        animadorPulso = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
            duration = 1200
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }
    }

    private fun carregarDadosDoFirebase() {
        val databaseRef = FirebaseDatabase.getInstance().getReference("corridas")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var distanciaTotalKm = 0.0
                var tempoTotalSegundos = 0L

                for (item in snapshot.children) {
                    try {
                        // Leitura segura campo a campo prevenindo crashes por incompatibilidade de tipos
                        val dist = item.child("distanciaKm").getValue(Double::class.java)
                            ?: item.child("distanciaKm").getValue(Long::class.java)?.toDouble()
                            ?: 0.0

                        val tempo = item.child("tempoSegundos").getValue(Long::class.java)
                            ?: item.child("tempoSegundos").getValue(Double::class.java)?.toLong()
                            ?: 0L

                        distanciaTotalKm += dist
                        tempoTotalSegundos += tempo
                    } catch (e: Exception) {
                        Log.e("PerfilActivity", "Erro ao processar corrida: ${e.message}")
                    }
                }

                // Atualiza as variáveis na interface gráfica
                atualizarInterface(distanciaTotalKm, tempoTotalSegundos)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PerfilActivity", "Erro Firebase: ${error.message}")
                Toast.makeText(this@PerfilActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
                atualizarInterface(0.0, 0L)
            }
        })
    }

    private fun atualizarInterface(distanciaKm: Double, tempoSegundos: Long) {
        // 1. Atualiza Distância Total
        txtDistanciaTotal.text = String.format(Locale.US, "%.1f KM", distanciaKm)

        // 2. Atualiza Tempo Total
        val horas = tempoSegundos / 3600
        val minutos = (tempoSegundos % 3600) / 60
        val segundosRestantes = tempoSegundos % 60

        txtTempoTotal.text = if (horas > 0) {
            String.format(Locale.US, "%02dh %02dm", horas, minutos)
        } else {
            String.format(Locale.US, "%02dm %02ds", minutos, segundosRestantes)
        }

        // 3. Atualiza Pace Médio (Minutos por Quilômetro)
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

    override fun onDestroy() {
        animadorPulso?.cancel()
        super.onDestroy()
    }
}