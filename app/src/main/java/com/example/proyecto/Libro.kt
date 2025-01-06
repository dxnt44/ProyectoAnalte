package com.example.proyecto

data class Libro(
    val idUsuario: Int,
    val titulo: String,
    val autor: String,
    val genero: String,
    val paginasTotales: Int,
    val paginaActual: Int,
    val calificacion: Int,
    val portada: String
)


