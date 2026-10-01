package com.kidsenglish.cartoons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kidsenglish.cartoons.ui.KidsCartoonsApp
import com.kidsenglish.cartoons.ui.theme.KidsCartoonsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KidsCartoonsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    KidsCartoonsApp()
                }
            }
        }
    }
}
