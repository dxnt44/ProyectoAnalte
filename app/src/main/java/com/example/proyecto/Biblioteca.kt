package com.example.proyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.databinding.BibliotecaBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Base64
import android.graphics.BitmapFactory
import android.util.Log

class Biblioteca : AppCompatActivity() {

    private lateinit var binding: BibliotecaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BibliotecaBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Integrar Compose para mostrar la lista de libros
        val idUsuario = getSharedPreferences("SesionUsuario", MODE_PRIVATE).getInt("id_usuario", -1)
        if (idUsuario != -1) {
            binding.composeContainer.setContent {
                MaterialTheme {
                    BibliotecaComposeScreen(idUsuario = idUsuario, apiService = RetrofitClient.apiService)
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
    apiService: ApiService
) {
    var libros by remember { mutableStateOf<List<Libro>>(emptyList()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val context = LocalContext.current // Usar LocalContext para obtener el contexto

    // Cargar los libros al iniciar la pantalla
    LaunchedEffect(Unit) {
        apiService.obtenerLibros(idUsuario).enqueue(object : Callback<ObtenerLibrosResponse> {
            override fun onResponse(call: Call<ObtenerLibrosResponse>, response: Response<ObtenerLibrosResponse>) {
                if (response.isSuccessful) {
                    val respuesta = response.body()
                    if (respuesta?.estado == "exito") {
                        libros = respuesta.libros
                        Log.d("BibliotecaComposeScreen", "Libros cargados: ${libros.size}")
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

    val filteredLibros by remember {
        derivedStateOf {
            libros.filter { libro ->
                libro.titulo.contains(searchQuery.text, ignoreCase = true) ||
                        libro.autor.contains(searchQuery.text, ignoreCase = true) ||
                        libro.genero.contains(searchQuery.text, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(searchQuery) { searchQuery = it }

        // Grid de libros con scroll
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredLibros.size) { index ->
                val libro = filteredLibros[index]
                LibroCard(libro = libro, onLibroClick = {
                    // Enviar solo ID del libro y ID del usuario
                    val intent = Intent(context, EditarLibro::class.java).apply {
                        putExtra("id", libro.id)
                        putExtra("id_usuario", idUsuario)
                    }
                    context.startActivity(intent)
                })
            }
        }
    }
}

@Composable
fun SearchBar(searchQuery: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD9B391), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Buscar:",
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.padding(end = 8.dp)
        )
        BasicTextField(
            value = searchQuery,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { /* Acción de búsqueda */ })
        )
    }
}

@Composable
fun LibroCard(libro: Libro, onLibroClick: (Libro) -> Unit) {
    val imageBitmap = remember(libro.portada) {
        decodeBase64ToImage(libro.portada)
    }
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onLibroClick(libro) },
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
