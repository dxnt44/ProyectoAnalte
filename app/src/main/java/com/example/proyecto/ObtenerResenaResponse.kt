package com.example.proyecto

data class ObtenerResenaResponse(
    val estado: String,
    val resena: ResenaData? = null
)

data class ResenaData(
    val idResena: Int,
    val texto: String
)


