package com.example.yigo.model

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String,
    val correo: String,
    val telefono: String,
    val tipo: String
)