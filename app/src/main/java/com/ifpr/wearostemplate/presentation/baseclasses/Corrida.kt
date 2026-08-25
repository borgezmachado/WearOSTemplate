package com.ifpr.wearostemplate.presentation.baseclasses

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Corrida(
    var id: String = "",
    var distanciaKm: Double = 0.0,
    var tempoSegundos: Long = 0L,
    var ritmoMedio: Double = 0.0,
    var velocidadeMedia: Double = 0.0,
    var calorias: Double = 0.0,
    var data: Long = 0L
)