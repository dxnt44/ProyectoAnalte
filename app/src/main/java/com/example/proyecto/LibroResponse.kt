package com.example.proyecto

data class Respuesta(
    val estado: String, // "exito" o "error"
    val mensaje: String // Mensaje adicional del servidor
)

