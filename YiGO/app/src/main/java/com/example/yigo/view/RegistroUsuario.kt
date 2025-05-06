package com.example.yigo.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yigo.R
import com.example.yigo.controller.RegisterController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistroUsuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_usuario)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a los elementos de la UI
        val nombreEditText = findViewById<EditText>(R.id.editTextText)
        val apellidoEditText = findViewById<EditText>(R.id.editTextText2)
        val telefonoEditText = findViewById<EditText>(R.id.editTextPhone)
        val correoEditText = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)
        val terminosCheckBox = findViewById<CheckBox>(R.id.checkBox)
        val registrarseButton = findViewById<Button>(R.id.button5)

        // Inicialmente el botón estará deshabilitado
        registrarseButton.isEnabled = false

        // Listener para el CheckBox
        terminosCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Habilitar o deshabilitar el botón según el estado del CheckBox
            registrarseButton.isEnabled = isChecked
        }

        // Listener para el botón de registro
        registrarseButton.setOnClickListener {
            // Obtener los valores de los campos
            val nombre = nombreEditText.text.toString()
            val apellido = apellidoEditText.text.toString()
            val telefono = telefonoEditText.text.toString()
            val correo = correoEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validar que los campos no estén vacíos
            if (nombre.isEmpty() || apellido.isEmpty() ||
                telefono.isEmpty() || correo.isEmpty() || password.isEmpty()) {

                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar formato de correo electrónico
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "Por favor ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar formato de teléfono
            if (!esTelefonoValido(telefono)) {
                Toast.makeText(this, "El teléfono debe tener exactamente 10 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar formato de contraseña
            if (!esContrasenaValida(password)) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Mostrar diálogo o indicador de carga
            Toast.makeText(this, "Registrando usuario...", Toast.LENGTH_SHORT).show()

            // Deshabilitar el botón para evitar múltiples envíos
            registrarseButton.isEnabled = false

            // Utilizamos corrutinas para manejar la operación asíncrona
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val resultado = withContext(Dispatchers.IO) {
                        try {
                            RegisterController.registrarPersona(
                                correo = correo,
                                password = password,
                                telefono = telefono,
                                nombre = nombre,
                                apellido = apellido
                            )
                            true // Consideramos éxito si no hay excepciones
                        } catch (e: Exception) {
                            e.printStackTrace()
                            false
                        }
                    }

                    if (resultado) {
                        Toast.makeText(this@RegistroUsuario, "Registro exitoso", Toast.LENGTH_SHORT).show()

                        // Navegar a MainActivity
                        val intent = Intent(this@RegistroUsuario, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Cierra esta actividad para que no vuelva atrás con el botón Back
                    } else {
                        Toast.makeText(
                            this@RegistroUsuario,
                            "Error al registrar. Inténtalo nuevamente",
                            Toast.LENGTH_LONG
                        ).show()
                        registrarseButton.isEnabled = terminosCheckBox.isChecked
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@RegistroUsuario,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    registrarseButton.isEnabled = terminosCheckBox.isChecked
                }
            }
        }
    }

    private fun esContrasenaValida(password: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}\$")
        return regex.matches(password)
    }

    private fun esTelefonoValido(telefono: String): Boolean {
        return telefono.matches(Regex("^\\d{10}\$"))
    }
}