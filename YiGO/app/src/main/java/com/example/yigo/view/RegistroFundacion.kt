package com.example.yigo.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yigo.R
import com.example.yigo.controller.RegisterController
import com.example.yigo.model.SupabaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class RegistroFundacion : AppCompatActivity() {

    private var rutFileUri: Uri? = null

    // Registrador para seleccionar archivo
    private val selectFileResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                rutFileUri = uri
                Toast.makeText(this, "Archivo RUT seleccionado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_fundacion)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a los elementos de la UI
        val razonSocialEditText = findViewById<EditText>(R.id.editTextText6)
        val nitEditText = findViewById<EditText>(R.id.editTextText7)
        val correoEditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword2)
        val telefonoEditText = findViewById<EditText>(R.id.editTextText8)
        val terminosCheckBox = findViewById<CheckBox>(R.id.checkBox2)
        val registrarseButton = findViewById<Button>(R.id.button6)
        val rutImageButton = findViewById<ImageButton>(R.id.imageButton2)

        // Inicialmente el botón estará deshabilitado
        registrarseButton.isEnabled = false

        // Listener para el CheckBox
        terminosCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Habilitar o deshabilitar el botón según el estado del CheckBox
            registrarseButton.isEnabled = isChecked
        }

        // Listener para el botón de seleccionar RUT
        rutImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
            }
            selectFileResultLauncher.launch(intent)
        }

        // Listener para el botón de registro
        registrarseButton.setOnClickListener {
            // Obtener los valores de los campos
            val razonSocial = razonSocialEditText.text.toString()
            val nit = nitEditText.text.toString()
            val correo = correoEditText.text.toString()
            val password = passwordEditText.text.toString()
            val telefono = telefonoEditText.text.toString()

            // Validar que los campos no estén vacíos
            if (razonSocial.isEmpty() || nit.isEmpty() ||
                telefono.isEmpty() || correo.isEmpty() ||
                password.isEmpty() || rutFileUri == null
            ) {
                Toast.makeText(
                    this,
                    "Por favor completa todos los campos y selecciona el RUT",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validar formato de correo electrónico
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(
                    this,
                    "Por favor ingresa un correo electrónico válido",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validar formato de NIT (9 dígitos exactos)
            if (!esNitValido(nit)) {
                Toast.makeText(this, "El NIT debe tener exactamente 9 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar formato de teléfono (10 dígitos exactos)
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
            Toast.makeText(this, "Registrando empresa...", Toast.LENGTH_SHORT).show()

            // Deshabilitar el botón para evitar múltiples envíos
            registrarseButton.isEnabled = false

            // Subir el RUT a Storage y luego registrar la empresa
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    // Subir archivo RUT
                    val rutUrl = withContext(Dispatchers.IO) {
                        subirArchivoRUT(rutFileUri!!)
                    }

                    if (rutUrl.isNullOrEmpty()) {
                        Toast.makeText(
                            this@RegistroFundacion,
                            "Error al subir el archivo RUT", Toast.LENGTH_SHORT
                        ).show()
                        registrarseButton.isEnabled = terminosCheckBox.isChecked
                        return@launch
                    }

                    // Registrar empresa
                    val resultado = withContext(Dispatchers.IO) {
                        try {
                            RegisterController.registrarEmpresa(
                                correo = correo,
                                password = password,
                                telefono = telefono,
                                razon_social = razonSocial,
                                nit = nit,
                                rut_url = rutUrl
                            )
                            true // Consideramos éxito si no hay excepciones
                        } catch (e: Exception) {
                            e.printStackTrace()
                            false
                        }
                    }

                    if (resultado) {
                        Toast.makeText(
                            this@RegistroFundacion,
                            "Registro exitoso",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navegar a MainActivity
                        val intent = Intent(this@RegistroFundacion, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Cierra esta actividad para que no vuelva atrás con el botón Back
                    } else {
                        Toast.makeText(
                            this@RegistroFundacion,
                            "Error al registrar. Inténtalo nuevamente",
                            Toast.LENGTH_LONG
                        ).show()
                        registrarseButton.isEnabled = terminosCheckBox.isChecked
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@RegistroFundacion,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    registrarseButton.isEnabled = terminosCheckBox.isChecked
                }
            }
        }
    }

    private suspend fun subirArchivoRUT(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Crear un nombre único para el archivo
                val fileName = "rut-empresas/rut_${UUID.randomUUID()}.pdf"

                // Leer el archivo de la URI
                val inputStream = contentResolver.openInputStream(uri)
                val fileBytes = inputStream?.readBytes()
                inputStream?.close()

                if (fileBytes != null) {
                    // Subir archivo a Supabase Storage
                    SupabaseService.storage
                        .from("documents") // Nombre del bucket en Storage
                        .upload(
                            path = fileName,
                            data = fileBytes,
                        ) {
                            upsert = true
                        }

                    // Obtener la URL pública del archivo
                    val publicUrl = SupabaseService.storage
                        .from("documents")
                        .publicUrl(fileName)

                    Log.d("Storage", "Archivo subido exitosamente: $publicUrl")
                    publicUrl
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("Storage", "Error al subir archivo: ${e.message}", e)
                null
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

    private fun esNitValido(nit: String): Boolean {
        return nit.matches(Regex("^\\d{9}\$"))
    }
}