package com.kidsenglish.cartoons.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.kidsenglish.cartoons.data.ApiService
import com.kidsenglish.cartoons.data.Cartoon
import com.kidsenglish.cartoons.data.LocalStore
import com.kidsenglish.cartoons.player.PlayerHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidsCartoonsApp() {
    val context = LocalContext.current
    val store = remember { LocalStore(context) }
    val scope = rememberCoroutineScope()
    var cartoons by remember { mutableStateOf(store.loadCartoons()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var selected by remember { mutableStateOf<Cartoon?>(null) }
    var downloadingId by remember { mutableStateOf<String?>(null) }

    // Fallback sample data (works offline / before Worker is deployed)
    val fallback = remember {
        listOf(
            Cartoon(
                id = "kidsongs-farm",
                title = "Kidsongs - A Day At Old MacDonald's Farm",
                description = "Fun English songs and farm animals for kids ages 4-8. Learn animal names and simple songs.",
                durationSec = 1800,
                videoUrl = "https://archive.org/download/kidsongs-series/A.%20Kidsongs%20A%20Day%20At%20Old%20MacDonald%20s%20Farm.mp4",
                thumbnailUrl = "https://archive.org/services/img/kidsongs-series",
                category = "Songs"
            ),
            Cartoon(
                id = "somewhere-dreamland",
                title = "Somewhere in Dreamland (1936)",
                description = "Classic public domain color cartoon. Soft story perfect for young children.",
                durationSec = 540,
                videoUrl = "https://archive.org/download/pdcartooncollection/Fleischer%20Color%20Classic%20Somewhere%20in%20Dreamland%201936)%20(old%20cartoon%20vintage%20public%20domain).mp4",
                thumbnailUrl = "https://archive.org/services/img/pdcartooncollection",
                category = "Classic"
            ),
            Cartoon(
                id = "little-lambkins",
                title = "Little Lambkins (1940)",
                description = "Fleischer Color Classic - gentle adventure for preschoolers.",
                durationSec = 480,
                videoUrl = "https://archive.org/download/pdcartooncollection/Fleischer%20Color%20Classic%20Little%20Lambkins%201940%20(old%20free%20cartoon%20public%20domain).mp4",
                thumbnailUrl = "https://archive.org/services/img/pdcartooncollection",
                category = "Classic"
            ),
            Cartoon(
                id = "old-mother-hubbard",
                title = "Old Mother Hubbard (1935)",
                description = "ComiColor cartoon based on the nursery rhyme. Great for English learning.",
                durationSec = 420,
                videoUrl = "https://archive.org/download/pdcartooncollection/COMICOLOR%20-%201935%20-%20_Old%20Mother%20Hubbard_.mp4",
                thumbnailUrl = "https://archive.org/services/img/pdcartooncollection",
                category = "Nursery"
            ),
            Cartoon(
                id = "simple-simon",
                title = "Simple Simon (ComiColor)",
                description = "Fun short cartoon with simple English dialogue and music.",
                durationSec = 360,
                videoUrl = "https://archive.org/download/pdcartooncollection/ComiColor_%20Simple%20Simon.mp4",
                thumbnailUrl = "https://archive.org/services/img/pdcartooncollection",
                category = "Nursery"
            )
        )
    }

    fun refresh() {
        scope.launch {
            isLoading = true
            error = null
            try {
                val api = ApiService.create()
                val resp = withContext(Dispatchers.IO) { api.getCartoons() }
                if (resp.cartoons.isNotEmpty()) {
                    cartoons = resp.cartoons
                    store.saveCartoons(resp.cartoons)
                } else {
                    cartoons = fallback
                    store.saveCartoons(fallback)
                }
            } catch (e: Exception) {
                if (cartoons.isEmpty()) {
                    cartoons = fallback
                    store.saveCartoons(fallback)
                }
                error = "Online list unavailable – showing offline samples"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        if (cartoons.isEmpty()) refresh()
        else if (System.currentTimeMillis() - store.getLastUpdate() > 24 * 60 * 60 * 1000L) refresh()
    }

    val filtered = remember(cartoons, searchQuery) {
        if (searchQuery.isBlank()) cartoons
        else cartoons.filter {
            it.title.contains(searchQuery, true) ||
            it.description.contains(searchQuery, true) ||
            it.category.contains(searchQuery, true)
        }
    }

    if (selected != null) {
        PlayerScreen(
            cartoon = selected!!,
            onBack = {
                PlayerHolder.pause()
                selected = null
            },
            isDownloaded = store.isDownloaded(selected!!.id),
            localPath = store.getLocalPath(selected!!.id),
            isDownloading = downloadingId == selected!!.id,
            onDownload = {
                scope.launch {
                    downloadingId = selected!!.id
                    try {
                        withContext(Dispatchers.IO) {
                            downloadFile(
                                selected!!.videoUrl,
                                File(store.getDownloadDir(), "${selected!!.id}.mp4")
                            )
                        }
                    } catch (_: Exception) {
                    } finally {
                        downloadingId = null
                    }
                }
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Kids English Cartoons", fontWeight = FontWeight.Bold)
                        Text("Ages 4–8 • Safe & Educational", fontSize = 12.sp, color = Color.White.copy(0.85f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                placeholder = { Text("Search cartoons...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            if (isLoading) {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp))
            }

            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { cartoon ->
                    CartoonCard(
                        cartoon = cartoon,
                        isDownloaded = store.isDownloaded(cartoon.id),
                        onClick = { selected = cartoon }
                    )
                }
            }
        }
    }
}

@Composable
fun CartoonCard(cartoon: Cartoon, isDownloaded: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = cartoon.thumbnailUrl.ifBlank { null },
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFE0B2)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(cartoon.title, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(cartoon.category, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                Text(
                    cartoon.description,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isDownloaded) {
                        Icon(Icons.Default.DownloadDone, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        Text(" Downloaded", fontSize = 11.sp, color = Color(0xFF4CAF50))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Ages ${cartoon.ageMin}–${cartoon.ageMax}", fontSize = 11.sp, color = Color.Gray)
                }
            }
            Icon(Icons.Default.PlayCircle, contentDescription = "Play", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
fun PlayerScreen(
    cartoon: Cartoon,
    onBack: () -> Unit,
    isDownloaded: Boolean,
    localPath: String?,
    isDownloading: Boolean,
    onDownload: () -> Unit
) {
    val context = LocalContext.current
    val playUrl = localPath ?: cartoon.videoUrl

    LaunchedEffect(playUrl) {
        PlayerHolder.play(context, playUrl, cartoon.title)
    }

    Column(Modifier.fillMaxSize().background(Color.Black)) {
        Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = PlayerHolder.get(ctx)
                        useController = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E)))
                )
                .padding(16.dp)
        ) {
            Text(cartoon.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            Text(cartoon.description, color = Color.White.copy(0.8f), fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Back")
                }
                if (!isDownloaded) {
                    Button(
                        onClick = onDownload,
                        enabled = !isDownloading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ECDC4))
                    ) {
                        if (isDownloading) {
                            CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Downloading...")
                        } else {
                            Icon(Icons.Default.Download, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Download")
                        }
                    }
                } else {
                    AssistChip(
                        onClick = {},
                        label = { Text("Saved offline") },
                        leadingIcon = { Icon(Icons.Default.DownloadDone, null, Modifier.size(18.dp)) }
                    )
                }
            }
        }
    }
}

private fun downloadFile(url: String, dest: File) {
    val client = OkHttpClient.Builder().build()
    val req = Request.Builder().url(url).build()
    client.newCall(req).execute().use { resp ->
        if (!resp.isSuccessful) throw Exception("HTTP ${resp.code}")
        resp.body?.byteStream()?.use { input ->
            FileOutputStream(dest).use { output ->
                input.copyTo(output)
            }
        }
    }
}
