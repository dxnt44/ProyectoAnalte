package com.example.proyecto

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.databinding.EditarLibroBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class EditarLibro : AppCompatActivity() {

    private lateinit var binding: EditarLibroBinding
    private var imagenSeleccionada: Bitmap? = null
    private var libroId: Int = -1
    private var idUsuario: Int = -1
    private var portadaActual: String? = null // Para almacenar la portada existente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = EditarLibroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("EditarLibro", "Iniciando actividad EditarLibro")

        // Configurar barra de navegación
        binding.imageView7.setOnClickListener {
            startActivity(Intent(this, Biblioteca::class.java))
        }

        binding.imageView4.setOnClickListener {
            startActivity(Intent(this, usuario::class.java))
        }

        binding.imageView5.setOnClickListener {
            startActivity(Intent(this, AnadirLibro::class.java))
        }

        // Obtener datos del Intent
        libroId = intent.getIntExtra("id", -1)
        idUsuario = intent.getIntExtra("id_usuario", -1)

        if (libroId == -1 || idUsuario == -1) {
            Toast.makeText(this, "Error: ID del libro o usuario no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Llamar a la API para obtener los datos del libro
        cargarDatosLibro()

        // Botón para subir imagen
        binding.button4.setOnClickListener {
            abrirGaleria()
        }

        // Guardar cambios al hacer clic en la imagen del cheque
        binding.imageView15.setOnClickListener {
            actualizarLibro()
        }
    }

    private fun cargarDatosLibro() {
        RetrofitClient.apiService.obtenerLibro(idUsuario, libroId).enqueue(object : Callback<DetalleLibroResponse> {
            override fun onResponse(call: Call<DetalleLibroResponse>, response: Response<DetalleLibroResponse>) {
                if (response.isSuccessful) {
                    val detalleLibro = response.body()
                    Log.d("cargarDatosLibro", "Respuesta de la API: $detalleLibro")
                    if (detalleLibro?.estado == "exito" && detalleLibro.libro != null) {
                        actualizarInterfaz(detalleLibro.libro)
                    } else {
                        Toast.makeText(this@EditarLibro, "Error al cargar los datos del libro", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Log.d("cargarDatosLibro", "Error en la respuesta del servidor: ${response.errorBody()?.string()}")
                    Toast.makeText(this@EditarLibro, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<DetalleLibroResponse>, t: Throwable) {
                Log.d("cargarDatosLibro", "Error de conexión: ${t.message}")
                Toast.makeText(this@EditarLibro, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }


    private fun actualizarInterfaz(libro: Libro) {
        binding.editTextText8.setText(libro.titulo)
        binding.editTextText9.setText(libro.autor)
        binding.editTextText10.setText(libro.genero)

        // Usar findViewById para las páginas
        val paginasTotalesField = findViewById<EditText>(R.id.editTotales)
        val paginaActualField = findViewById<EditText>(R.id.editActual)
        val numerototales = libro.paginasTotales.toString()
        val numeroactual = libro.paginaActual.toString()

        Log.d("ActualizarInterfaz", "Páginas Totales desde la API: $numerototales")
        Log.d("ActualizarInterfaz", "Página Actual desde la API: $numeroactual")

        paginasTotalesField.setText(numerototales)
        paginaActualField.setText(numeroactual)

        binding.ratingBar3.rating = libro.calificacion
        portadaActual = libro.portada // Guardar la portada actual

        libro.portada?.let {
            val decodedBytes = Base64.decode(it, Base64.DEFAULT)
            val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            binding.imageView10.setImageBitmap(decodedBitmap)
        }

        binding.progressBar.max = libro.paginasTotales
        binding.progressBar.progress = libro.paginaActual
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
            binding.imageView10.setImageBitmap(imagenSeleccionada)
        }
    }

    private fun convertirImagenABase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private fun actualizarLibro() {
        if (libroId == -1 || idUsuario == -1) {
            Toast.makeText(this, "Error al obtener la información del libro o usuario", Toast.LENGTH_SHORT).show()
            return
        }

        val titulo = binding.editTextText8.text.toString()
        val autor = binding.editTextText9.text.toString()
        val genero = binding.editTextText10.text.toString()

        // Usar findViewById para obtener los valores de las páginas
        val paginasTotales = findViewById<EditText>(R.id.editTotales).text.toString().toIntOrNull() ?: 0
        val paginaActual = findViewById<EditText>(R.id.editActual).text.toString().toIntOrNull() ?: 0
        val calificacion = binding.ratingBar3.rating

        if (titulo.isEmpty() || autor.isEmpty() || genero.isEmpty() || paginasTotales <= 0) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val imagenBase64 = if (imagenSeleccionada != null) {
            convertirImagenABase64(imagenSeleccionada!!)
        } else {
            portadaActual // Mantener la portada actual si no se selecciona una nueva
        }

        RetrofitClient.apiService.actualizarLibro(
            id = libroId,
            titulo = titulo,
            autor = autor,
            genero = genero,
            paginasTotales = paginasTotales,
            paginaActual = paginaActual,
            calificacion = calificacion,
            portada = imagenBase64 ?: "" // Mandar string vacío si no hay ninguna portada
        ).enqueue(object : Callback<RespuestaBase> {
            override fun onResponse(call: Call<RespuestaBase>, response: Response<RespuestaBase>) {
                if (response.isSuccessful && response.body()?.estado == "exito") {
                    Toast.makeText(this@EditarLibro, "Libro actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditarLibro, "Error al actualizar el libro", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RespuestaBase>, t: Throwable) {
                Toast.makeText(this@EditarLibro, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
