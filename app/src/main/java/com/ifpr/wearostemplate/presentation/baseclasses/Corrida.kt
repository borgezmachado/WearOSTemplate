package com.ifpr.wearostemplate.presentation.baseclasses

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Corrida(
    val id: String = "",
    val distanciaKm: Double = 0.0,
    val tempoSegundos: Long = 0L,
    val data: String = ""
)