package com.example.yigo.model

import kotlinx.serialization.Serializable

@Serializable
data class Empresa(
    val id: String,
    val razon_social: String,
    val nit: String,
    val rut_url: String
)