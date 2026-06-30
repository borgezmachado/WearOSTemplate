package com.ifpr.wearostemplate.presentation

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ifpr.wearostemplate.R

class PerfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val txtNome = findViewById<TextView>(R.id.txtNome)
        val txtDistanciaTotal = findViewById<TextView>(R.id.txtDistanciaTotal)
        val txtPaceMedio = findViewById<TextView>(R.id.txtPaceMedio)
        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar) // Captura o botão voltar

        // Textos padrão do perfil
        txtNome.text = "PILOTO XLR8"
        txtDistanciaTotal.text = "145.8 KM"
        txtPaceMedio.text = "05:30 /KM"

        // Evento de clique para fechar a tela de perfil e voltar para a Main
        btnVoltar.setOnClickListener {
            finish() // Fecha a tela atual e retorna para a tela que te chamou
        }
    }
}