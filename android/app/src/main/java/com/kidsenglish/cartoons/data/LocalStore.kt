package com.kidsenglish.cartoons.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class LocalStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("kids_cartoons", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val downloadDir = File(context.filesDir, "downloads").apply { mkdirs() }

    fun saveCartoons(list: List<Cartoon>) {
        prefs.edit().putString("cartoons_json", gson.toJson(list)).apply()
        prefs.edit().putLong("last_update", System.currentTimeMillis()).apply()
    }

    fun loadCartoons(): List<Cartoon> {
        val json = prefs.getString("cartoons_json", null) ?: return emptyList()
        val type = object : TypeToken<List<Cartoon>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getLastUpdate(): Long = prefs.getLong("last_update", 0)

    fun isDownloaded(id: String): Boolean {
        return File(downloadDir, "$id.mp4").exists()
    }

    fun getLocalPath(id: String): String? {
        val f = File(downloadDir, "$id.mp4")
        return if (f.exists()) f.absolutePath else null
    }

    fun markDownloaded(id: String) {
        // file already written by downloader
    }

    fun getDownloadDir(): File = downloadDir
}
