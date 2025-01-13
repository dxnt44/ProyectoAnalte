package com.example.proyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.databinding.ResenaBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Resena : AppCompatActivity() {

    private lateinit var binding: ResenaBinding
    private var idLibro: Int = -1
    private var idUsuario: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ResenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLibro = intent.getIntExtra("id_libro", -1)
        idUsuario = intent.getIntExtra("id_usuario", -1)

        if (idLibro == -1 || idUsuario == -1) {
            Toast.makeText(this, "Datos no válidos", Toast.LENGTH_SHORT).show()
            finish()
        }

        val tituloLibro = intent.getStringExtra("titulo_libro") ?: "Libro desconocido"

        binding.composeContainer.setContent {
            MaterialTheme {
                ResenaScreen(idLibro, idUsuario, tituloLibro, this@Resena)
            }
        }


        // Configuración de navegación
        configurarNavegacion()
    }

    private fun configurarNavegacion() {
        findViewById<ImageView>(R.id.imageView7).setOnClickListener {
            startActivity(Intent(this, Biblioteca::class.java))
        }
        findViewById<ImageView>(R.id.imageView4).setOnClickListener {
            startActivity(Intent(this, usuario::class.java))
        }
        findViewById<ImageView>(R.id.imageView5).setOnClickListener {
            startActivity(Intent(this, AnadirLibro::class.java))
        }
    }
}

@Composable
fun RoundedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = ""
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE1DFDD), shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) { innerTextField ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (value.isEmpty()) {
                Text(hint, style = TextStyle(color = Color.Gray, fontSize = 18.sp))
            }
            innerTextField()
        }
    }
}

@Composable
fun RoundedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Color(0xFFD9B391)),
        modifier = modifier
            .background(Color(0xFFD9B391), shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
            .padding(8.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
fun ResenaScreen(idLibro: Int, idUsuario: Int, tituloLibro: String, context: Context) {
    var reseña by remember { mutableStateOf(TextFieldValue("")) }
    var editMode by remember { mutableStateOf(true) }
    var reseñaId by remember { mutableStateOf(-1) } // ID de la reseña existente (si la hay)

    // Cargar reseña al iniciar la pantalla
    LaunchedEffect(Unit) {
        cargarResena(idLibro, idUsuario) { id, texto ->
            if (id != -1) {
                reseñaId = id
                reseña = TextFieldValue(texto)
                editMode = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center // Centrar horizontalmente
        ) {
            Text(
                text = "Reseña de $tituloLibro",
                style = TextStyle(
                    fontSize = 18.sp, // Cambiar el tamaño de la fuente
                    color = Color.Black,
                    fontStyle = FontStyle.Italic, // Estilo de la fuente: cursiva
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (editMode) {
            // Cuadro de texto editable
            RoundedTextField(
                value = reseña.text,
                onValueChange = { reseña = TextFieldValue(it) },
                hint = "Escribe tu reseña aquí",
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            RoundedButton(
                text = if (reseñaId == -1) "Crear" else "Guardar",
                onClick = {
                    if (reseñaId == -1) {
                        crearResena(context, idLibro, idUsuario, reseña.text) { success ->
                            if (success) {
                                editMode = false
                                Toast.makeText(context, "Reseña creada correctamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al crear la reseña", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        editarResena(context, reseñaId, reseña.text) { success ->
                            if (success) {
                                editMode = false
                                Toast.makeText(context, "Reseña editada correctamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al editar la reseña", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End)
            )
        } else {
            // Mostrar reseña como texto no editable
            Text(
                text = reseña.text,
                style = TextStyle(fontSize = 14.sp, color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE1DFDD), shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            RoundedButton(
                text = "Editar",
                onClick = { editMode = true },
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}


fun cargarResena(idLibro: Int, idUsuario: Int, onResult: (Int, String) -> Unit) {
    RetrofitClient.apiService.obtenerResena(idLibro, idUsuario).enqueue(object : Callback<ObtenerResenaResponse> {
        override fun onResponse(call: Call<ObtenerResenaResponse>, response: Response<ObtenerResenaResponse>) {
            if (response.isSuccessful) {
                val respuesta = response.body()
                Log.d("cargarResena", "Respuesta recibida: $respuesta")
                if (respuesta?.estado == "exito" && respuesta.resena != null) {
                    onResult(respuesta.resena.idResena, respuesta.resena.texto)
                } else {
                    onResult(-1, "")
                }
            } else {
                Log.e("cargarResena", "Error en la respuesta: ${response.errorBody()?.string()}")
                onResult(-1, "")
            }
        }

        override fun onFailure(call: Call<ObtenerResenaResponse>, t: Throwable) {
            Log.e("cargarResena", "Error de conexión: ${t.message}")
            onResult(-1, "")
        }
    })
}

fun editarResena(context: Context, idResena: Int, texto: String, onResult: (Boolean) -> Unit) {
    if (texto.isEmpty()) {
        Toast.makeText(context, "La reseña no puede estar vacía", Toast.LENGTH_SHORT).show()
        onResult(false)
        return
    }

    RetrofitClient.apiService.editarResena(idResena, texto).enqueue(object : Callback<Respuesta> {
        override fun onResponse(call: Call<Respuesta>, response: Response<Respuesta>) {
            if (response.isSuccessful && response.body()?.estado == "exito") {
                onResult(true)
            } else {
                Log.e("editarResena", "Error en la respuesta: ${response.errorBody()?.string()}")
                onResult(false)
            }
        }

        override fun onFailure(call: Call<Respuesta>, t: Throwable) {
            Log.e("editarResena", "Error de conexión: ${t.message}")
            onResult(false)
        }
    })
}

fun crearResena(context: Context, idLibro: Int, idUsuario: Int, texto: String, onResult: (Boolean) -> Unit) {
    if (texto.isEmpty()) {
        Toast.makeText(context, "La reseña no puede estar vacía", Toast.LENGTH_SHORT).show()
        onResult(false)
        return
    }

    Log.d("crearResena", "Enviando datos: idLibro=$idLibro, idUsuario=$idUsuario, texto=$texto")
    RetrofitClient.apiService.crearResena(idLibro, idUsuario, texto).enqueue(object : Callback<Respuesta> {
        override fun onResponse(call: Call<Respuesta>, response: Response<Respuesta>) {
            if (response.isSuccessful && response.body()?.estado == "exito") {
                Log.d("crearResena", "Respuesta exitosa: ${response.body()}")
                onResult(true)
            } else {
                Log.e("crearResena", "Error en la respuesta: ${response.errorBody()?.string()}")
                onResult(false)
            }
        }

        override fun onFailure(call: Call<Respuesta>, t: Throwable) {
            Log.e("crearResena", "Error de conexión: ${t.message}")
            onResult(false)
        }
    })
}
