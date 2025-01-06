package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.databinding.SeleccionAnadirBinding

class AnadirLibro : AppCompatActivity() {

    private lateinit var binding: SeleccionAnadirBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SeleccionAnadirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar barra de navegación manual
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

        // Botón central para añadir libro
        val agregarLibroButton = findViewById<ImageView>(R.id.imageView5)
        agregarLibroButton.setOnClickListener {
            val intent = Intent(this, AnadirLibro::class.java)
            startActivity(intent)
        }

        // Botón principal para ir al formulario de agregar libro
        binding.btnAgregarLibro.setOnClickListener {
            val intent = Intent(this, AnadirManual::class.java)
            startActivity(intent)
        }
    }
}
