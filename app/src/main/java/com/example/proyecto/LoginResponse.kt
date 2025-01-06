package com.example.proyecto

data class LoginResponse(
    val estado: String,
    val mensaje: String,
    val id_usuario: Int? = null,
    val nombre: String? = null,
    val correo: String? = null // Nuevo campo
)
