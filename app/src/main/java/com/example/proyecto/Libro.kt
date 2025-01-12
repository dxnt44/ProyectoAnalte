package com.example.proyecto

import com.google.gson.annotations.SerializedName

data class Libro(
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_usuario")
    val idUsuario: Int,
    @SerializedName("titulo")
    val titulo: String,
    @SerializedName("autor")
    val autor: String,
    @SerializedName("genero")
    val genero: String,
    @SerializedName("paginas_totales")
    val paginasTotales: Int,
    @SerializedName("pagina_actual")
    val paginaActual: Int,
    @SerializedName("calificacion")
    val calificacion: Float,
    @SerializedName("portada")
    val portada: String
)
