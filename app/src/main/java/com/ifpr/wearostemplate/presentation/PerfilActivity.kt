package com.ifpr.wearostemplate.presentation

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.BlurMaskFilter
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
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

        // Mapeamento dos elementos
        txtDistanciaTotal = findViewById(R.id.txtDistanciaTotal)
        txtTempoTotal = findViewById(R.id.txtTempoTotal)
        txtPaceMedio = findViewById(R.id.txtPaceMedio)
        txtNome = findViewById(R.id.txtNome)
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil)
        btnVoltar = findViewById(R.id.btnVoltar)

        // 1. Aplica o efeito Neon Glow de alta definição no Nome do Atleta (sem retângulo feio)
        aplicarEfeitoGlowNeon(txtNome)

        // 2. Animação de entrada fluida para os elementos do perfil
        val animEntrada = AnimationUtils.loadAnimation(this, R.anim.fade_slide_up)
        txtNome.startAnimation(animEntrada)
        imgFotoPerfil.startAnimation(animEntrada)
        txtDistanciaTotal.startAnimation(animEntrada)
        txtTempoTotal.startAnimation(animEntrada)
        txtPaceMedio.startAnimation(animEntrada)

        // 3. Animação de pulso suave na Foto de Perfil
        iniciarAnimacaoPulso(imgFotoPerfil)

        // 4. Clique no Botão Voltar com micro-interação elástica
        btnVoltar.setOnClickListener { view ->
            view.animate().scaleX(0.85f).scaleY(0.85f).setDuration(90).withEndAction {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(90).start()
                finish()
            }.start()
        }

        carregarDadosDoFirebase()
    }

    private fun aplicarEfeitoGlowNeon(textView: TextView) {
        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        textView.paint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.SOLID)
    }

    private fun iniciarAnimacaoPulso(view: View) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.96f, 1.04f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.96f, 1.04f)

        ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
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
                    val corrida = item.getValue(Corrida::class.java)
                    if (corrida != null) {
                        distanciaTotalKm += corrida.distanciaKm
                        tempoTotalSegundos += corrida.tempoSegundos
                    }
                }

                atualizarInterface(distanciaTotalKm, tempoTotalSegundos)
            }

            override fun onCancelled(error: DatabaseError) {
                atualizarInterface(0.0, 0L)
            }
        })
    }

    private fun atualizarInterface(distanciaKm: Double, tempoSegundos: Long) {
        txtDistanciaTotal.text = String.format(Locale.US, "%.1f KM", distanciaKm)

        val horas = tempoSegundos / 3600
        val minutos = (tempoSegundos % 3600) / 60
        val segundosRestantes = tempoSegundos % 60

        txtTempoTotal.text = if (horas > 0) {
            String.format(Locale.US, "%02dh %02dm", horas, minutos)
        } else {
            String.format(Locale.US, "%02dm %02ds", minutos, segundosRestantes)
        }

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