package com.ajantha.apsa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ajantha.apsa.navigation.AppNavDisplay
import com.ajantha.apsa.theme.APSATheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            APSATheme {
                AppNavDisplay()
            }
        }

        /*val scanner = AppScanner(this)
        lifecycleScope.launch {
            val apps = scanner.getInstalledApps()
            exportAll(
                context = this@MainActivity,
                apps = apps
            )
        }*/
        initDir()
    }

    private fun initDir() {
        val dir = File(getExternalFilesDir(null), ".keep")
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

}