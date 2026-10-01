package com.kidsenglish.cartoons.ui

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
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

    val fallback = remember { modernFallback() }

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
                error = "Online list unavailable – showing modern offline list"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        // Always prefer modern list on first open after update
        if (cartoons.isEmpty() || cartoons.none { it.isYouTube() || it.category.contains("Peppa", true) }) {
            cartoons = fallback
            store.saveCartoons(fallback)
        }
        refresh()
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
                if (!selected!!.canDownload()) return@PlayerScreen
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
                        Text("Peppa • Paw Patrol • Super Wings • Ages 4–8", fontSize = 12.sp, color = Color.White.copy(0.85f))
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
                placeholder = { Text("Search Peppa, Paw Patrol, Super Wings...") },
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

private fun modernFallback(): List<Cartoon> = listOf(
    Cartoon(
        id = "peppa-sharing",
        title = "Peppa Pig – Sharing Is Caring (Full Episodes)",
        description = "Official Peppa Pig English episodes. Perfect for ages 4–8.",
        youtubeId = "e5Ef8rOUWUo",
        thumbnailUrl = "https://img.youtube.com/vi/e5Ef8rOUWUo/hqdefault.jpg",
        category = "Peppa Pig",
        type = "youtube"
    ),
    Cartoon(
        id = "peppa-summer",
        title = "Peppa Pig – Summer Adventures",
        description = "Official full episodes compilation from Peppa Pig channel.",
        youtubeId = "6-xa1WJ4cjc",
        thumbnailUrl = "https://img.youtube.com/vi/6-xa1WJ4cjc/hqdefault.jpg",
        category = "Peppa Pig",
        type = "youtube"
    ),
    Cartoon(
        id = "peppa-secret-door",
        title = "Peppa Pig – Secret Door & Mystery Stairs",
        description = "Official Peppa Pig English full episodes.",
        youtubeId = "Uc8knK0ONgk",
        thumbnailUrl = "https://img.youtube.com/vi/Uc8knK0ONgk/hqdefault.jpg",
        category = "Peppa Pig",
        type = "youtube"
    ),
    Cartoon(
        id = "paw-mighty-twins",
        title = "PAW Patrol – Mighty Pups Meet the Mighty Twins",
        description = "Official PAW Patrol full episode. English for kids 4–8.",
        youtubeId = "NcrX0Kv9YTQ",
        thumbnailUrl = "https://img.youtube.com/vi/NcrX0Kv9YTQ/hqdefault.jpg",
        category = "PAW Patrol",
        type = "youtube"
    ),
    Cartoon(
        id = "paw-jungle",
        title = "PAW Patrol – Jungle Pups Hidden Jungle",
        description = "Official PAW Patrol full episode from official channel.",
        youtubeId = "D33Tg3A-L4E",
        thumbnailUrl = "https://img.youtube.com/vi/D33Tg3A-L4E/hqdefault.jpg",
        category = "PAW Patrol",
        type = "youtube"
    ),
    Cartoon(
        id = "paw-sea-octopus",
        title = "PAW Patrol – Sea Patrol Baby Octopus",
        description = "Official full episode. Great English for preschoolers.",
        youtubeId = "bFkuy5yAMig",
        thumbnailUrl = "https://img.youtube.com/vi/bFkuy5yAMig/hqdefault.jpg",
        category = "PAW Patrol",
        type = "youtube"
    ),
    Cartoon(
        id = "paw-fire-monster",
        title = "PAW Patrol – Fire Rescue Movie Monster",
        description = "Official PAW Patrol English episode.",
        youtubeId = "EbJBUniF99A",
        thumbnailUrl = "https://img.youtube.com/vi/EbJBUniF99A/hqdefault.jpg",
        category = "PAW Patrol",
        type = "youtube"
    ),
    Cartoon(
        id = "superwings-delivery",
        title = "Super Wings – The Delivery King",
        description = "Official Super Wings English episode. Adventure for ages 4–8.",
        youtubeId = "JLZW0G3ryeM",
        thumbnailUrl = "https://img.youtube.com/vi/JLZW0G3ryeM/hqdefault.jpg",
        category = "Super Wings",
        type = "youtube"
    ),
    Cartoon(
        id = "superwings-heritage",
        title = "Super Wings – Exploring World Heritage",
        description = "Official Super Wings best episodes compilation (English).",
        youtubeId = "Eza1Xyijikc",
        thumbnailUrl = "https://img.youtube.com/vi/Eza1Xyijikc/hqdefault.jpg",
        category = "Super Wings",
        type = "youtube"
    ),
    Cartoon(
        id = "superwings-ep02",
        title = "Super Wings – Great Gondolas (ENG)",
        description = "Official Super Wings English episode.",
        youtubeId = "aWZJXi3nuFM",
        thumbnailUrl = "https://img.youtube.com/vi/aWZJXi3nuFM/hqdefault.jpg",
        category = "Super Wings",
        type = "youtube"
    ),
    Cartoon(
        id = "superwings-bath",
        title = "Super Wings – Boonying's Bath Time (ENG)",
        description = "Official Super Wings English episode for young kids.",
        youtubeId = "C1dg0IqouRA",
        thumbnailUrl = "https://img.youtube.com/vi/C1dg0IqouRA/hqdefault.jpg",
        category = "Super Wings",
        type = "youtube"
    ),
    Cartoon(
        id = "peppa-walkie",
        title = "Peppa Pig – Walkie Talkies (1 Hour)",
        description = "Official Peppa Pig full episodes compilation.",
        youtubeId = "ESCtnG1Jxrk",
        thumbnailUrl = "https://img.youtube.com/vi/ESCtnG1Jxrk/hqdefault.jpg",
        category = "Peppa Pig",
        type = "youtube"
    )
)

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
                model = cartoon.thumbnailUrl.ifBlank {
                    val yid = cartoon.resolvedYoutubeId()
                    if (yid.isNotBlank()) "https://img.youtube.com/vi/$yid/hqdefault.jpg" else null
                },
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
                        Text(" Offline", fontSize = 11.sp, color = Color(0xFF4CAF50))
                    } else if (cartoon.isYouTube()) {
                        Text("Watch online (official)", fontSize = 11.sp, color = Color.Gray)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Ages ${cartoon.ageMin}–${cartoon.ageMax}", fontSize = 11.sp, color = Color.Gray)
                }
            }
            Icon(Icons.Default.PlayCircle, contentDescription = "Play", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
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
    val isYt = cartoon.isYouTube()
    val yid = cartoon.resolvedYoutubeId()

    LaunchedEffect(cartoon.id) {
        if (!isYt) {
            val playUrl = localPath ?: cartoon.videoUrl
            if (playUrl.isNotBlank()) PlayerHolder.play(context, playUrl, cartoon.title)
        }
    }

    Column(Modifier.fillMaxSize().background(Color.Black)) {
        Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
            if (isYt && yid.isNotBlank()) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.mediaPlaybackRequiresUserGesture = false
                            webChromeClient = WebChromeClient()
                            webViewClient = WebViewClient()
                            val html = """
                                <html><body style="margin:0;background:#000;">
                                <iframe width="100%" height="100%" style="position:absolute;top:0;left:0;width:100%;height:100%;"
                                  src="https://www.youtube.com/embed/$yid?playsinline=1&rel=0&modestbranding=1"
                                  frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                  allowfullscreen></iframe>
                                </body></html>
                            """.trimIndent()
                            loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "utf-8", null)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
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
        }
        Column(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E))))
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
                if (cartoon.canDownload()) {
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
                } else {
                    AssistChip(
                        onClick = {},
                        label = { Text("Official stream") },
                        leadingIcon = { Icon(Icons.Default.PlayArrow, null, Modifier.size(18.dp)) }
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
            FileOutputStream(dest).use { output -> input.copyTo(output) }
        }
    }
}
