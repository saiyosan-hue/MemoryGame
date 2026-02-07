package com.example.memorygame

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var tvMoves: TextView
    private lateinit var btnMain: Button

    private var cards: List<Int> = emptyList()
    private var openPositions: MutableList<Int> = mutableListOf()
    private var isLocked: BooleanArray = booleanArrayOf()
    private var moves: Int = 0

    // Доступ к SharedPreferences
    private val prefs: SharedPreferences by lazy {
        getSharedPreferences("game_settings", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Get saved theme
        val isDarkTheme = prefs.getBoolean("dark_theme", false)
        Log.d("ThemeDebug", "GameActivity: Saved dark_theme = $isDarkTheme")

        // Apply theme BEFORE super.onCreate()
        if (isDarkTheme) {
            setTheme(R.style.Theme_MemoryGame_Dark)
            Log.d("ThemeDebug", "GameActivity: Applied DARK theme")
        } else {
            setTheme(R.style.Theme_MemoryGame)
            Log.d("ThemeDebug", "GameActivity: Applied LIGHT theme")
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        btnMain = findViewById(R.id.btnMain)
        btnMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        gridView = findViewById(R.id.gridView)
        tvMoves = findViewById(R.id.tvMoves)

        val level = prefs.getInt("level", 1)

        cards = generateCards(level)
        isLocked = BooleanArray(cards.size) { false }

        val adapter = CardAdapter(this, cards, isLocked, openPositions)
        gridView.adapter = adapter

        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (openPositions.size < 2 && !isLocked[position]) {
                flipCard(view, position)
                openPositions.add(position)

                if (openPositions.size == 2) {
                    checkMatch()
                }
            }
        }
    }

    private fun generateCards(level: Int): List<Int> {
        val pairCount = when (level) {
            1 -> 4
            2 -> 8
            3 -> 18
            else -> 4
        }
        val imageIds = List(pairCount) { it }
        return (imageIds + imageIds).shuffled()
    }

    private fun flipCard(view: View, position: Int) {
        val ivCard: ImageView = view.findViewById(R.id.ivCard)
        val resourceId = resources.getIdentifier(
            "icon_${cards[position]}",
            "drawable",
            packageName
        )
        ivCard.setImageResource(resourceId)
    }

    private fun checkMatch() {
        val (pos1, pos2) = openPositions

        if (cards[pos1] == cards[pos2]) {
            isLocked[pos1] = true
            isLocked[pos2] = true
            openPositions.clear()

            if (isLocked.all { it }) {
                Toast.makeText(this, getString(R.string.you_won), Toast.LENGTH_LONG).show()
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                openPositions.clear()
                val adapter = CardAdapter(this, cards, isLocked, openPositions)
                gridView.adapter = adapter
            }, 1000)
        }

        moves++
        tvMoves.text = getString(R.string.moves, moves)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("moves", moves)
        outState.putIntegerArrayList("openPositions", ArrayList(openPositions))
        outState.putBooleanArray("isLocked", isLocked)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        moves = savedInstanceState.getInt("moves")
        openPositions = savedInstanceState.getIntegerArrayList("openPositions") ?: mutableListOf()
        isLocked = savedInstanceState.getBooleanArray("isLocked") ?: booleanArrayOf()

        tvMoves.text = getString(R.string.moves, moves)
        val adapter = CardAdapter(this, cards, isLocked, openPositions)
        gridView.adapter = adapter
    }
}