package com.example.proyecto

import com.google.gson.annotations.SerializedName

data class DetalleLibroResponse(
    @SerializedName("estado")
    val estado: String, // "exito" o "error"

    @SerializedName("libro")
    val libro: Libro?, // Detalles del libro en caso de éxito

    @SerializedName("mensaje")
    val mensaje: String? = null // Mensaje adicional del servidor
)
