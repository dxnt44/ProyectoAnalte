/*package com.example.proyecto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro)
    }
}

package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.databinding.RegistroBinding

class registro : AppCompatActivity() {

    // Declarar el objeto de ViewBinding
    private lateinit var binding: RegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar ViewBinding
        binding = RegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el botón para navegar a UsuarioActivity
        binding.btnUsuario.setOnClickListener {
            val intent = Intent(this, usuario::class.java)
            startActivity(intent)
        }
    }
}*/

package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.databinding.RegistroBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class registro : AppCompatActivity() {

    // Declarar el objeto de ViewBinding
    private lateinit var binding: RegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar ViewBinding
        binding = RegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el botón de registro
        binding.btnUsuario.setOnClickListener {
            val nombre = binding.editTextText2.text.toString()
            val correo = binding.editTextText3.text.toString()
            val contrasena = binding.editTextText5.text.toString()
            val confirmContrasena = binding.editTextText6.text.toString()

            // Verificar que los campos no estén vacíos
            if (nombre.isNotEmpty() && correo.isNotEmpty() && contrasena.isNotEmpty() && confirmContrasena.isNotEmpty()) {
                if (contrasena == confirmContrasena) {
                    registrarUsuario(nombre, correo, contrasena)
                } else {
                    Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registrarUsuario(nombre: String, correo: String, contrasena: String) {
        val call = RetrofitClient.apiService.registrarUsuario("registro", nombre, correo, contrasena)
        call.enqueue(object : Callback<RegistroResponse> {
            override fun onResponse(call: Call<RegistroResponse>, response: Response<RegistroResponse>) {
                if (response.isSuccessful) {
                    val registroResponse = response.body()
                    if (registroResponse != null && registroResponse.estado == "exito") {
                        // Guardar sesión en SharedPreferences
                        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putInt("id_usuario", registroResponse.id_usuario ?: -1)
                        editor.putString("nombre", nombre)
                        editor.putString("correo", correo)
                        editor.putBoolean("sesion_activa", true)
                        editor.apply()

                        // Redirigir al usuario a la pantalla principal
                        val intent = Intent(this@registro, usuario::class.java)
                        startActivity(intent)
                        finish()

                        Toast.makeText(this@registro, registroResponse.mensaje, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@registro, registroResponse?.mensaje ?: "Error desconocido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@registro, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegistroResponse>, t: Throwable) {
                Toast.makeText(this@registro, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

