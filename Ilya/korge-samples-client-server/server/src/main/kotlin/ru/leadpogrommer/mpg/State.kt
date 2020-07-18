package ru.leadpogrommer.mpg

import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.ContextualSerialization

class State {
    val m = Mutex()


    private val players = mutableMapOf<Long, Player>()
    private var nextID = 1L

    fun getPlayer(id: Long) = players[id]!!

    fun getIterator() = players.iterator()

    fun getSt() = players as Map<@ContextualSerialization Long, @ContextualSerialization Player>


    fun registerPlayer(e: Player) {
        e.id = nextID
        nextID++
        players[e.id] = e
    }

    fun deletePlayer(id: Long) {
        players.remove(id)
    }
}