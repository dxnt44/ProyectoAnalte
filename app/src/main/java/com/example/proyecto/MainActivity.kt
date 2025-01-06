package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar si la sesión está activa
        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val sesionActiva = sharedPreferences.getBoolean("sesion_activa", false)

        if (sesionActiva) {
            // Redirigir a la pantalla principal si la sesión está activa
            val intent = Intent(this, usuario::class.java)
            startActivity(intent)
            finish()
        }

        // Configurar el botón de registro
        binding.btnRegistro.setOnClickListener {
            val intent = Intent(this, registro::class.java)
            startActivity(intent)
        }

        // Configurar el botón de login
        binding.button2.setOnClickListener {
            val correo = binding.editTextText.text.toString()
            val contrasena = binding.editTextText4.text.toString()

            if (correo.isNotEmpty() && contrasena.isNotEmpty()) {
                login(correo, contrasena)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(correo: String, contrasena: String) {
        val call = RetrofitClient.apiService.login("login", correo, contrasena)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.estado == "exito") {
                        // Guardar sesión en SharedPreferences
                        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putInt("id_usuario", loginResponse.id_usuario ?: -1)
                        editor.putString("nombre", loginResponse.nombre)
                        editor.putString("correo", loginResponse.correo) // Guardar correo
                        editor.putBoolean("sesion_activa", true)
                        editor.apply()

                        // Redirigir a la actividad principal del usuario
                        val intent = Intent(this@MainActivity, usuario::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, loginResponse?.mensaje ?: "Error desconocido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
