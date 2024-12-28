package com.example.proyecto

data class RegistroResponse(
    val estado: String, // "exito" o "error"
    val mensaje: String // Mensaje de éxito o error
)
