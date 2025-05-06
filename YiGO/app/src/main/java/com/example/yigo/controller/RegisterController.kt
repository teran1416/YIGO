package com.example.yigo.controller

import android.util.Log
import com.example.yigo.model.Usuario
import com.example.yigo.model.Persona
import com.example.yigo.model.Empresa
import com.example.yigo.model.SupabaseService
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.HttpRequestException

object RegisterController {

    suspend fun registrarPersona(
        correo: String,
        password: String,
        telefono: String,
        nombre: String,
        apellido: String
    ): Boolean {
        return try {
            Log.d("Registro", "Intentando registrar usuario persona con correo: $correo")
            SupabaseService.auth.signUpWith(Email) {
                this.email = correo.trim()
                this.password = password
            }

            val userId = SupabaseService.auth.currentUserOrNull()?.id
                ?: return false // Retorna false si no se pudo obtener el ID

            Log.d("Registro", "Usuario registrado: $userId")

            // Insertar en tabla usuarios
            val insertUsuarioResponse = SupabaseService.database.from("usuarios").insert(
                Usuario(
                    id = userId,
                    correo = correo,
                    telefono = telefono,
                    tipo = "persona"
                )
            )
            Log.d("Registro", "Registro en tabla 'usuarios' completado: $insertUsuarioResponse")

            // Insertar en tabla personas
            val insertPersonaResponse = SupabaseService.database.from("personas").insert(
                Persona(
                    id = userId,
                    nombre = nombre,
                    apellido = apellido
                )
            )

            Log.d("Registro", "Persona registrada en la base de datos: $insertPersonaResponse")
            true // Retorna true si el registro fue exitoso
        } catch (e: HttpRequestException) {
            Log.e("Registro", "Error HTTP: ${e.message}", e)
            false
        } catch (e: Exception) {
            Log.e("Registro", "Error general: ${e.message}", e)
            false
        }
    }

    suspend fun registrarEmpresa(
        correo: String,
        password: String,
        telefono: String,
        razon_social: String,
        nit: String,
        rut_url: String
    ): Boolean {
        return try {
            Log.d("Registro", "Intentando registrar usuario empresa con correo: $correo")
            SupabaseService.auth.signUpWith(Email) {
                this.email = correo.trim()
                this.password = password
            }

            val userId = SupabaseService.auth.currentUserOrNull()?.id
                ?: return false // Retorna false si no se pudo obtener el ID

            // Insertar en tabla usuarios
            SupabaseService.database.from("usuarios").insert(
                Usuario(
                    id = userId,
                    correo = correo,
                    telefono = telefono,
                    tipo = "empresa"
                )
            )

            // Insertar en tabla empresas
            SupabaseService.database.from("empresas").insert(
                Empresa(
                    id = userId,
                    razon_social = razon_social,
                    nit = nit,
                    rut_url = rut_url
                )
            )

            Log.d("Registro", "Empresa registrada exitosamente")
            true
        } catch (e: HttpRequestException) {
            Log.e("Registro", "Error HTTP: ${e.message}", e)
            false
        } catch (e: Exception) {
            Log.e("Registro", "Error general: ${e.message}", e)
            false
        }
    }

}