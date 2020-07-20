package server

import shared.Player
import kotlin.random.Random

class Engine {
    private val players = mutableListOf<Player>()
    private var id = 0

    private fun movePlayer(pl: Player) {
        pl.x = Random.nextInt(100)
        pl.y = Random.nextInt(100)
    }

    fun register(): Player {
        val player = Player(id++)
        movePlayer(player)
        players.add(player)
        return player
    }

    fun remove(pl: Player) = players.remove(pl)

    fun tick() = players.forEach { movePlayer(it) }

    fun getPlayers() = ArrayList(players)
}