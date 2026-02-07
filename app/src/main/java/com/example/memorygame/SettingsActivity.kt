package com.example.memorygame

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.edit
import android.content.Intent

class SettingsActivity : AppCompatActivity() {

    private lateinit var rgLevel: RadioGroup
    private lateinit var switchTheme: SwitchCompat
    private lateinit var btnSaveSettings: Button
    private lateinit var btnMain: Button

    private val prefs: SharedPreferences by lazy {
        getSharedPreferences("game_settings", MODE_PRIVATE)
    }

    companion object {
        const val KEY_LEVEL = "level"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Get saved theme
        val isDarkTheme = prefs.getBoolean("dark_theme", false)

        // Apply theme BEFORE super.onCreate()
        if (isDarkTheme) {
            setTheme(R.style.Theme_MemoryGame_Dark)
            Log.d("ThemeDebug", "Settings: Applied DARK theme")
        } else {
            setTheme(R.style.Theme_MemoryGame)
            Log.d("ThemeDebug", "Settings: Applied LIGHT theme")
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        rgLevel = findViewById(R.id.rgLevel)
        switchTheme = findViewById(R.id.switchTheme)
        btnSaveSettings = findViewById(R.id.btnSaveSettings)
        btnMain = findViewById(R.id.btnMain)

        // Levels
        val level = savedInstanceState?.getInt(KEY_LEVEL) ?: prefs.getInt("level", 1)
        when (level) {
            1 -> rgLevel.check(R.id.rbLevel1)
            2 -> rgLevel.check(R.id.rbLevel2)
            3 -> rgLevel.check(R.id.rbLevel3)
        }

        switchTheme.isChecked = isDarkTheme  // Use saved theme

        // Listener of the theme toggler
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit { putBoolean("dark_theme", isChecked) }
            Log.d("ThemeDebug", "Settings: dark_theme saved = $isChecked")

            // Reload Activity with new theme
            recreate()
        }

        btnSaveSettings.setOnClickListener {
            val selectedLevel = when (rgLevel.checkedRadioButtonId) {
                R.id.rbLevel1 -> 1
                R.id.rbLevel2 -> 2
                R.id.rbLevel3 -> 3
                else -> 1
            }
            prefs.edit { putInt("level", selectedLevel) }
        }

        btnMain.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_LEVEL, when (rgLevel.checkedRadioButtonId) {
            R.id.rbLevel1 -> 1
            R.id.rbLevel2 -> 2
            R.id.rbLevel3 -> 3
            else -> 1
        })
    }
}