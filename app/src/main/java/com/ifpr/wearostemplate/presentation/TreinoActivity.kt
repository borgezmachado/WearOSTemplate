package com.ifpr.wearostemplate.presentation

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.BlurMaskFilter
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.AnimationUtils
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
    private lateinit var txtAppName: TextView
    private lateinit var txtDistanciaTreino: TextView
    private lateinit var txtPaceTreino: TextView
    private lateinit var txtCaloriasTreino: TextView
    private lateinit var pulseLive: View
    private lateinit var pulseStop: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContentView(R.layout.activity_treino)

        // Mapeamento dos componentes da interface
        chronometer = findViewById(R.id.chronometer)
        btnStop = findViewById(R.id.btnStop)
        txtAppName = findViewById(R.id.txtAppName)
        txtData = findViewById(R.id.txtData)
        txtDistanciaTreino = findViewById(R.id.txtDistanciaTreino)
        txtPaceTreino = findViewById(R.id.txtPaceTreino)
        txtCaloriasTreino = findViewById(R.id.txtCaloriasTreino)
        pulseLive = findViewById(R.id.pulseLive)
        pulseStop = findViewById(R.id.pulseStop)

        // 1. Aplica o efeito Neon Glow de alta definição (sem bordas pretas)
        aplicarEfeitoGlowNeon(txtAppName, 8f)
        aplicarEfeitoGlowNeon(chronometer, 12f)

        // 2. Animação de entrada fluida para os elementos da tela
        val animEntrada = AnimationUtils.loadAnimation(this, R.anim.fade_slide_up)
        findViewById<View>(R.id.containerTreino)?.startAnimation(animEntrada)

        // 3. Ativa animação de pulso contínuo nos elementos visuais
        iniciarAnimacaoPulso(pulseLive, 600)  // Pulso rápido no indicador de status
        iniciarAnimacaoPulso(pulseStop, 1000) // Pulso no anel do botão Stop

        // Define a data atual dinamicamente em caixa alta
        val sdfData = SimpleDateFormat("EEE, dd 'DE' MMM", Locale.getDefault())
        txtData.text = sdfData.format(Date()).uppercase()

        // Inicia a contagem do cronômetro nativo
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()

        // Clique no Botão Stop com micro-interação elástica
        btnStop.setOnClickListener { view ->
            view.animate().scaleX(0.85f).scaleY(0.85f).setDuration(90).withEndAction {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(90).start()
                salvarCorridaEFinalizar()
            }.start()
        }
    }

    private fun aplicarEfeitoGlowNeon(view: TextView, blurRadius: Float) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        view.paint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.SOLID)
    }

    private fun iniciarAnimacaoPulso(view: View, duracao: Long) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.85f, 1.25f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.85f, 1.25f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.6f, 0.15f)

        ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY, alpha).apply {
            duration = duracao
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }
    }

    private fun salvarCorridaEFinalizar() {
        chronometer.stop()

        val tempoSegundos = (SystemClock.elapsedRealtime() - chronometer.base) / 1000
        val distanciaKm = 2.5 // Dado simulado (será substituído por sensores GPS futuramente)

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