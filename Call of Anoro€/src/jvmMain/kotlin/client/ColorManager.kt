package client

import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import kotlin.random.Random

class ColorManager {

    val colors = hashMapOf<Int, RGBA>()

    var counter = 0
    val colorsList = listOf("#c0c0c0", "#800000", "#ffa500", "#00ff00", "#00ffff", "#000080", "#ff00ff")

    fun getColor(team: Int): RGBA{
        if(!colors.containsKey(team)){
            colors[team] = Colors[colorsList[counter++]]
        }
        return colors[team]!!
    }
}