package com.example.proyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.databinding.BibliotecaBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Base64
import android.graphics.BitmapFactory
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration

class Biblioteca : AppCompatActivity() {

    private lateinit var binding: BibliotecaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BibliotecaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar barra de navegación
        binding.imageView7.setOnClickListener {
            val intent = Intent(this, Biblioteca::class.java)
            startActivity(intent)
        }

        binding.imageView4.setOnClickListener {
            val intent = Intent(this, usuario::class.java)
            startActivity(intent)
        }

        binding.imageView5.setOnClickListener {
            val intent = Intent(this, AnadirLibro::class.java)
            startActivity(intent)
        }

        // Integrar Compose para mostrar la lista de libros
        val idUsuario = getSharedPreferences("SesionUsuario", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario != -1) {
            binding.composeContainer.setContent {
                MaterialTheme {
                    BibliotecaComposeScreen(idUsuario = idUsuario, apiService = RetrofitClient.apiService, context = this)
                }
            }
        } else {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun BibliotecaComposeScreen(
    idUsuario: Int,
    apiService: ApiService,
    context: Context
) {
    var libros by remember { mutableStateOf<List<Libro>>(emptyList()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // Cargar los libros al iniciar la pantalla
    LaunchedEffect(Unit) {
        apiService.obtenerLibros(idUsuario).enqueue(object : Callback<ObtenerLibrosResponse> {
            override fun onResponse(call: Call<ObtenerLibrosResponse>, response: Response<ObtenerLibrosResponse>) {
                if (response.isSuccessful) {
                    val respuesta = response.body()
                    if (respuesta?.estado == "exito") {
                        libros = respuesta.libros
                    } else {
                        Toast.makeText(context, "Error: ${respuesta?.estado}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ObtenerLibrosResponse>, t: Throwable) {
                Toast.makeText(context, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    val filteredLibros = libros.filter {
        it.titulo.contains(searchQuery.text, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Buscar:",
            color = Color.Gray,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        BasicTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                //.padding(8.dp)
        )

        // Grid de libros con scroll
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredLibros.size) { index ->
                LibroCard(libro = filteredLibros[index])
            }
        }
    }
}

@Composable
fun LibroCard(libro: Libro) {
    val imageBitmap = remember {
        decodeBase64ToImage(libro.portada)
    }
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageBitmap?.let {
            Image(
                bitmap = it,
                contentDescription = libro.titulo,
                modifier = Modifier.size(100.dp)
            )
        }
        Text(
            text = libro.titulo,
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 10.sp
        )
        Text(
            text = libro.autor,
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 10.sp
        )
    }
}

fun decodeBase64ToImage(base64: String): ImageBitmap? {
    return try {
        val imageBytes = Base64.decode(base64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}
