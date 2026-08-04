package com.ifpr.wearostemplate.presentation

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.BlurMaskFilter
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.ifpr.wearostemplate.R

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContentView(R.layout.activity_main)

        val txtTitulo = findViewById<TextView>(R.id.txtTitulo)
        val txtSubtitulo = findViewById<TextView>(R.id.txtSubtitulo)
        val btnPlay = findViewById<ImageButton>(R.id.btnPlay)
        val btnPerfil = findViewById<Button>(R.id.btnPerfil)
        val pulseRing = findViewById<View>(R.id.pulseRing)

        // Aplica o Efeito Glow/Neon sem a caixa retangular
        aplicarEfeitoGlowNeon(txtTitulo)

        // Animações de entrada para os elementos
        val animEntrada = AnimationUtils.loadAnimation(this, R.anim.fade_slide_up)
        txtTitulo.startAnimation(animEntrada)
        txtSubtitulo.startAnimation(animEntrada)
        btnPerfil.startAnimation(animEntrada)

        // Pulso contínuo no botão Play
        iniciarAnimacaoPulso(pulseRing)

        btnPlay.setOnClickListener { view ->
            view.animate().scaleX(0.85f).scaleY(0.85f).setDuration(90).withEndAction {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(90).start()
                startActivity(Intent(this, TreinoActivity::class.java))
            }.start()
        }

        btnPerfil.setOnClickListener { view ->
            view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(90).withEndAction {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(90).start()
                startActivity(Intent(this, PerfilActivity::class.java))
            }.start()
        }
    }

    private fun aplicarEfeitoGlowNeon(textView: TextView) {
        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        textView.paint.maskFilter = BlurMaskFilter(12f, BlurMaskFilter.Blur.SOLID)
    }

    private fun iniciarAnimacaoPulso(view: View) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f, 1.25f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f, 1.25f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.5f, 0.1f)

        ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY, alpha).apply {
            duration = 900
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }
    }
}