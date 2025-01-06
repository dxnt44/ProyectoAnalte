package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class usuario : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.usuario)

        // Configurar barra de navegación
        val bibliotecaButton = findViewById<ImageView>(R.id.imageView7)
        bibliotecaButton.setOnClickListener {
            val intent = Intent(this, Biblioteca::class.java)
            startActivity(intent)
        }

        val usuarioButton = findViewById<ImageView>(R.id.imageView4)
        usuarioButton.setOnClickListener {
            Toast.makeText(this, "Ya estás en la pantalla de Usuario.", Toast.LENGTH_SHORT).show()
        }

        val agregarLibroButton = findViewById<ImageView>(R.id.imageView5)
        agregarLibroButton.setOnClickListener {
            val intent = Intent(this, AnadirLibro::class.java)
            startActivity(intent)
        }

        // Cargar estadísticas del usuario
        cargarEstadisticas()
    }

    private fun cargarEstadisticas() {
        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val idUsuario = sharedPreferences.getInt("id_usuario", -1)

        if (idUsuario == -1) {
            Toast.makeText(this, "Error al obtener el ID del usuario", Toast.LENGTH_SHORT).show()
            return
        }

        val call = RetrofitClient.apiService.obtenerEstadisticas(idUsuario)
        call.enqueue(object : Callback<EstadisticasResponse> {
            override fun onResponse(call: Call<EstadisticasResponse>, response: Response<EstadisticasResponse>) {
                if (response.isSuccessful) {
                    val estadisticas = response.body()
                    if (estadisticas != null) {
                        // Actualizar los valores en la interfaz
                        findViewById<TextView>(R.id.textTotal).text = "Total de Libros: ${estadisticas.total}"
                        findViewById<TextView>(R.id.textLeidos).text = "Libros Leídos: ${estadisticas.leidos}"
                        findViewById<TextView>(R.id.textSinLeer).text = "Libros Sin Leer: ${estadisticas.sin_leer}"
                    }
                } else {
                    Toast.makeText(this@usuario, "Error al cargar estadísticas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EstadisticasResponse>, t: Throwable) {
                Toast.makeText(this@usuario, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
