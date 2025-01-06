package com.example.proyecto

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.databinding.AnadirManualBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.Base64

class AnadirManual : AppCompatActivity() {

    private lateinit var binding: AnadirManualBinding
    private var imagenSeleccionada: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar el binding para acceder a los elementos de la vista
        binding = AnadirManualBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar barra de navegación
        val bibliotecaButton = findViewById<ImageView>(R.id.imageView7)
        bibliotecaButton.setOnClickListener {
            val intent = Intent(this, Biblioteca::class.java)
            startActivity(intent)
        }

        val usuarioButton = findViewById<ImageView>(R.id.imageView4)
        usuarioButton.setOnClickListener {
            val intent = Intent(this, usuario::class.java)
            startActivity(intent)
        }

        val agregarLibroButton = findViewById<ImageView>(R.id.imageView5)
        agregarLibroButton.setOnClickListener {
            val intent = Intent(this, AnadirLibro::class.java)
            startActivity(intent)
        }

        // Botón para subir imagen desde la galería
        binding.button4.setOnClickListener {
            abrirGaleria()
        }

        // Guardar datos del libro al hacer clic en la imagen del cheque
        binding.imageView15.setOnClickListener {
            guardarLibro()
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imagenSeleccionada = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            binding.imageView10.setImageBitmap(imagenSeleccionada) // Mostrar la imagen seleccionada
        }
    }

    private fun convertirImagenABase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return Base64.getEncoder().encodeToString(imageBytes)
    }

    private fun guardarLibro() {
        val titulo = binding.editTextText9.text.toString()
        val autor = binding.editTextText8.text.toString()
        val genero = binding.editTextText10.text.toString()
        val paginasTotales = binding.editTextText11.text.toString()
        val paginaActual = binding.editTextText12.text.toString()
        val calificacion = binding.ratingBar3.rating

        // Validaciones
        if (titulo.isEmpty() || autor.isEmpty() || genero.isEmpty() || paginasTotales.isEmpty() || paginaActual.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos obligatorios.", Toast.LENGTH_SHORT).show()
            return
        }

        val imagenBase64 = if (imagenSeleccionada != null) {
            convertirImagenABase64(imagenSeleccionada!!)
        } else {
            convertirImagenABase64(BitmapFactory.decodeResource(resources, R.drawable.libro_default))
        }

        // Crear objeto para enviar al servidor
        val libro = Libro(
            titulo = titulo,
            autor = autor,
            genero = genero,
            paginasTotales = paginasTotales.toInt(),
            paginaActual = paginaActual.toInt(),
            calificacion = calificacion.toInt(),
            portada = imagenBase64
        )

        // Llamada al servicio web
        val call = RetrofitClient.apiService.agregarLibro(libro)
        call.enqueue(object : Callback<Respuesta> {
            override fun onResponse(call: Call<Respuesta>, response: Response<Respuesta>) {
                if (response.isSuccessful && response.body()?.estado == "exito") {
                    Toast.makeText(this@AnadirManual, "com.example.proyecto.Libro guardado correctamente", Toast.LENGTH_SHORT).show()
                    finish() // Finalizar actividad
                } else {
                    Toast.makeText(this@AnadirManual, "Error al guardar el libro", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Respuesta>, t: Throwable) {
                Toast.makeText(this@AnadirManual, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
