package com.example.proyecto

data class ObtenerLibrosResponse(
    val estado: String,
    val libros: List<Libro>
)
