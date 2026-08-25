package com.ifpr.wearostemplate.presentation

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BlurMaskFilter
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.database.FirebaseDatabase
import com.ifpr.wearostemplate.R
import com.ifpr.wearostemplate.presentation.baseclasses.Corrida
import java.util.Locale

class MainActivity : ComponentActivity() {

    // LOCALIZAÇÃO
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        5000L
    )
        .setMinUpdateIntervalMillis(2000L)
        .setMinUpdateDistanceMeters(2f)
        .build()

    // COMPONENTES DA INTERFACE
    private var txtTitulo: TextView? = null
    private var txtSubtitulo: TextView? = null
    private var txtTempo: TextView? = null
    private var txtDistancia: TextView? = null
    private var txtRitmo: TextView? = null

    private var btnStart: Button? = null
    private var btnStop: Button? = null
    private var btnPlay: ImageButton? = null
    private var btnPerfil: Button? = null
    private var pulseRing: View? = null

    // DADOS DA CORRIDA
    private var corridaEmAndamento = false
    private var tempoInicio: Long = 0L
    private var distanciaTotalMetros = 0.0
    private var ultimaLocalizacao: Location? = null

    // PERMISSÃO DE LOCALIZAÇÃO
    private val solicitarPermissaoLocalizacao =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permitido ->
            if (permitido) {
                iniciarCorrida()
            } else {
                Toast.makeText(
                    this,
                    "A localização é necessária para registrar a corrida.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // TIMER
    private val atualizadorTempo = object : Runnable {
        override fun run() {
            if (!corridaEmAndamento) return
            val tempoDecorrido = SystemClock.elapsedRealtime() - tempoInicio
            atualizarTempoNaTela(tempoDecorrido)
            txtTempo?.postDelayed(this, 1000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContentView(R.layout.activity_main)

        // VINCULAÇÃO DE COMPONENTES
        txtTitulo = findViewById(R.id.txtTitulo)
        txtSubtitulo = findViewById(R.id.txtSubtitulo)
        txtTempo = findViewById(R.id.txtTempo)
        txtDistancia = findViewById(R.id.txtDistancia)
        txtRitmo = findViewById(R.id.txtRitmo)

        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        btnPlay = findViewById(R.id.btnPlay)
        btnPerfil = findViewById(R.id.btnPerfil)
        pulseRing = findViewById(R.id.pulseRing)

        // ANIMAÇÕES E EFEITOS
        txtTitulo?.let { aplicarEfeitoGlowNeon(it) }

        val animEntrada = AnimationUtils.loadAnimation(this, R.anim.fade_slide_up)
        txtTitulo?.startAnimation(animEntrada)
        txtSubtitulo?.startAnimation(animEntrada)
        btnPerfil?.startAnimation(animEntrada)

        pulseRing?.let { iniciarAnimacaoPulso(it) }

        // LOCALIZAÇÃO E EVENTOS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        configurarLocationCallback()
        configurarBotoes()
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

    private fun configurarBotoes() {
        btnPerfil?.setOnClickListener { view ->
            view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(90).withEndAction {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(90).start()
                startActivity(Intent(this, PerfilActivity::class.java))
            }.start()
        }

        btnPlay?.setOnClickListener { view ->
            view.animate().scaleX(0.85f).scaleY(0.85f).setDuration(90).withEndAction {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(90).start()
                startActivity(Intent(this, TreinoActivity::class.java))
            }.start()
        }

        btnStart?.setOnClickListener { verificarPermissaoEIniciarCorrida() }
        btnStop?.setOnClickListener { encerrarCorrida() }
    }

    private fun verificarPermissaoEIniciarCorrida() {
        if (temPermissaoLocalizacao()) {
            iniciarCorrida()
        } else {
            solicitarPermissaoLocalizacao.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun iniciarCorrida() {
        if (corridaEmAndamento) {
            Toast.makeText(this, "A corrida já foi iniciada.", Toast.LENGTH_SHORT).show()
            return
        }
        if (!temPermissaoLocalizacao()) return

        corridaEmAndamento = true
        distanciaTotalMetros = 0.0
        ultimaLocalizacao = null
        tempoInicio = SystemClock.elapsedRealtime()

        txtTempo?.text = "00:00"
        txtDistancia?.text = "0.00 km"
        txtRitmo?.text = "-- min/km"

        txtTempo?.post(atualizadorTempo)
        iniciarAtualizacoesLocalizacao()

        Toast.makeText(this, "Corrida iniciada!", Toast.LENGTH_SHORT).show()
    }

    private fun configurarLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (!corridaEmAndamento) return
                for (localizacao in locationResult.locations) {
                    processarNovaLocalizacao(localizacao)
                }
            }
        }
    }

    private fun iniciarAtualizacoesLocalizacao() {
        val permissaoFine = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permissaoCoarse = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissaoFine && !permissaoCoarse) return

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                mainLooper
            )
        } catch (e: SecurityException) {
            Toast.makeText(this, "Não foi possível acessar a localização.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processarNovaLocalizacao(novaLocalizacao: Location) {
        val anterior = ultimaLocalizacao
        if (anterior != null) {
            val deslocamentoMetros = anterior.distanceTo(novaLocalizacao)
            if (deslocamentoMetros >= 2.0) {
                distanciaTotalMetros += deslocamentoMetros
            }
        }
        ultimaLocalizacao = novaLocalizacao
        atualizarDadosNaTela()
    }

    private fun atualizarDadosNaTela() {
        val distanciaKm = distanciaTotalMetros / 1000.0
        txtDistancia?.text = String.format(Locale.getDefault(), "%.2f km", distanciaKm)

        val tempoSegundos = (SystemClock.elapsedRealtime() - tempoInicio) / 1000

        if (distanciaKm > 0.0 && tempoSegundos > 0) {
            val ritmo = calcularRitmoMedio(tempoSegundos, distanciaKm)
            txtRitmo?.text = formatarRitmo(ritmo)
        }
    }

    private fun atualizarTempoNaTela(tempoMilissegundos: Long) {
        val segundosTotais = tempoMilissegundos / 1000
        val minutos = segundosTotais / 60
        val segundos = segundosTotais % 60
        txtTempo?.text = String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos)
    }

    private fun calcularRitmoMedio(tempoSegundos: Long, distanciaKm: Double): Double {
        if (distanciaKm <= 0.0) return 0.0
        return (tempoSegundos / 60.0) / distanciaKm
    }

    private fun formatarRitmo(ritmo: Double): String {
        if (ritmo <= 0.0) return "-- min/km"
        val minutos = ritmo.toInt()
        val segundos = ((ritmo - minutos) * 60).toInt()
        return String.format(Locale.getDefault(), "%d:%02d min/km", minutos, segundos)
    }

    private fun calcularVelocidadeMedia(distanciaKm: Double, tempoSegundos: Long): Double {
        if (tempoSegundos <= 0L) return 0.0
        return distanciaKm / (tempoSegundos / 3600.0)
    }

    private fun calcularCalorias(distanciaKm: Double, pesoKg: Double): Double {
        return distanciaKm * pesoKg * 1.036
    }

    private fun encerrarCorrida() {
        if (!corridaEmAndamento) {
            Toast.makeText(this, "Nenhuma corrida em andamento.", Toast.LENGTH_SHORT).show()
            return
        }

        corridaEmAndamento = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
        txtTempo?.removeCallbacks(atualizadorTempo)

        val tempoMilissegundos = SystemClock.elapsedRealtime() - tempoInicio
        val tempoSegundos = tempoMilissegundos / 1000
        val distanciaKm = distanciaTotalMetros / 1000.0

        if (distanciaKm <= 0.0) {
            Toast.makeText(this, "Nenhuma distância foi registrada.", Toast.LENGTH_SHORT).show()
            return
        }

        salvarCorrida(distanciaKm, tempoSegundos)
    }

    private fun salvarCorrida(distanciaKm: Double, tempoSegundos: Long) {
        val ritmoMedio = calcularRitmoMedio(tempoSegundos, distanciaKm)
        val velocidadeMedia = calcularVelocidadeMedia(distanciaKm, tempoSegundos)
        val calorias = calcularCalorias(distanciaKm, 70.0)

        val referencia = FirebaseDatabase.getInstance().getReference("corridas")
        val id = referencia.push().key ?: return

        val corrida = Corrida(
            id = id,
            distanciaKm = distanciaKm,
            tempoSegundos = tempoSegundos,
            ritmoMedio = ritmoMedio,
            velocidadeMedia = velocidadeMedia,
            calorias = calorias,
            data = System.currentTimeMillis()
        )

        referencia.child(id).setValue(corrida)
            .addOnSuccessListener {
                Toast.makeText(this, "Corrida salva com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar a corrida.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun temPermissaoLocalizacao(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        txtTempo?.removeCallbacks(atualizadorTempo)
        super.onDestroy()
    }
}