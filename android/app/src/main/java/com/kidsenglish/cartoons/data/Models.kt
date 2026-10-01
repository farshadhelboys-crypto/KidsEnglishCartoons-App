package com.kidsenglish.cartoons.data

data class Cartoon(
    val id: String,
    val title: String,
    val description: String,
    val ageMin: Int = 4,
    val ageMax: Int = 8,
    val durationSec: Int = 0,
    val videoUrl: String,
    val thumbnailUrl: String = "",
    val category: String = "Educational",
    val language: String = "English",
    val source: String = "Archive.org"
)

data class CartoonsResponse(
    val updatedAt: String = "",
    val cartoons: List<Cartoon> = emptyList()
)
