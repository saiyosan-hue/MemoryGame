package com.example.memorygame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class CardAdapter(
    private val context: Context,
    private val cardList: List<Int>,
    private val isLocked: BooleanArray,
    private val openPositions: List<Int>
) : BaseAdapter() {

    override fun getCount(): Int = cardList.size

    override fun getItem(position: Int): Any = cardList[position]

    override fun getItemId(position: Int): Long = position.toLong()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_card, parent, false)

        val ivCard = view.findViewById<ImageView>(R.id.ivCard)

        if (isLocked[position] || openPositions.contains(position)) {
            val resourceId = context.resources.getIdentifier(
                "icon_${cardList[position]}",
                "drawable",
                context.packageName
            )
            ivCard.setImageResource(resourceId)
        } else {
            ivCard.setImageResource(R.drawable.card_back)
        }

        return view
    }
}
