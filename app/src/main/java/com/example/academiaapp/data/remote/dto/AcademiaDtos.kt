package com.example.academiaapp.data.remote.dto

data class AcademiaDto(
    val id: Int,
    val nombre: String
)

data class AcademiaResponse(
    val ok: Boolean,
    val result: AcademiaDto
)
