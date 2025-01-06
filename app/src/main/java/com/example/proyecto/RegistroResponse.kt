package com.example.proyecto

data class RegistroResponse(
    val estado: String, // "exito" o "error"
    val mensaje: String, // Mensaje adicional del servidor
    val id_usuario: Int? = null, // ID del usuario registrado
    val nombre: String? = null,  // Nombre del usuario registrado
    val correo: String? = null   // Correo del usuario registrado
)
