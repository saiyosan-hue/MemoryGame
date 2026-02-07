package com.example.memorygame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {

    private lateinit var btnStartGame: Button
    private lateinit var btnSettings: Button

    private val prefs: SharedPreferences by lazy {
        getSharedPreferences("game_settings", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Get saved theme
        val isDarkTheme = prefs.getBoolean("dark_theme", false)
        Log.d("ThemeDebug", "MainActivity: Saved dark_theme = $isDarkTheme")

        // Apply theme BEFORE super.onCreate()
        if (isDarkTheme) {
            setTheme(R.style.Theme_MemoryGame_Dark)
            Log.d("ThemeDebug", "MainActivity: Applied DARK theme")
        } else {
            setTheme(R.style.Theme_MemoryGame)
            Log.d("ThemeDebug", "MainActivity: Applied LIGHT theme")
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartGame = findViewById(R.id.btnStartGame)
        btnSettings = findViewById(R.id.btnSettings)

        btnStartGame.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}