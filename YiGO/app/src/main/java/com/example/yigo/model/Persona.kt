package com.example.yigo.model

import kotlinx.serialization.Serializable

@Serializable
data class Persona(
    val id: String,
    val nombre: String,
    val apellido: String
)