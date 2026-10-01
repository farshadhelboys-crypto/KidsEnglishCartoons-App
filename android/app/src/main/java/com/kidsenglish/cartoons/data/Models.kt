package com.kidsenglish.cartoons.data

data class Cartoon(
    val id: String,
    val title: String,
    val description: String,
    val ageMin: Int = 4,
    val ageMax: Int = 8,
    val durationSec: Int = 0,
    val videoUrl: String = "",
    val youtubeId: String = "",
    val thumbnailUrl: String = "",
    val category: String = "Educational",
    val language: String = "English",
    val source: String = "YouTube Official",
    val type: String = "youtube" // youtube | direct
) {
    fun isYouTube(): Boolean = type == "youtube" || youtubeId.isNotBlank() ||
        videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")

    fun resolvedYoutubeId(): String {
        if (youtubeId.isNotBlank()) return youtubeId
        val u = videoUrl
        return when {
            "youtu.be/" in u -> u.substringAfter("youtu.be/").substringBefore("?").substringBefore("&")
            "v=" in u -> u.substringAfter("v=").substringBefore("&")
            else -> ""
        }
    }

    fun canDownload(): Boolean = type == "direct" && videoUrl.startsWith("http") && !isYouTube()
}

data class CartoonsResponse(
    val updatedAt: String = "",
    val cartoons: List<Cartoon> = emptyList()
)
