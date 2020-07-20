package server

import shared.*
import kotlin.math.*
import kotlin.random.Random

fun border (a : Double, small : Double, big : Double) : Double
{
    if (a < small)
        return small
    else if (a > big)
        return big
    else
        return a
}

class Engine
{
    var playerMap = mutableMapOf<Int, Player>()
    var nextFreeId = 0
    val maxX = Config.maxX.toDouble()
    val maxY = Config.maxY.toDouble()
    val radius = Config.radius

    fun getNewTarget(playerId : Int) : Int?
    {
        if (playerId in playerMap.keys && playerMap[playerId]!!.getTargetId() != null)
            return playerMap[playerId]!!.getTargetId()

        val chosenTargets = mutableListOf<Int>()

        for (i in playerMap.keys)
        {
            if (playerMap[i]!!.getTargetId() != null)
                chosenTargets.add(playerMap[i]!!.getTargetId()!!)

        }

        var i = 0

        while ((i < nextFreeId) && (i !in playerMap || i in chosenTargets || i == playerId || playerMap[i]!!.getTargetId() == playerId))
            i++
        if (i in playerMap.map {it.value.getId()})
            return i
        return null
    }

    fun registerPlayer() : Player
    {
        val x = Random.nextDouble(maxX)
        val y = Random.nextDouble(maxY)

        val newPlayer = Player(nextFreeId, getNewTarget(nextFreeId), x, y)

        playerMap[nextFreeId] = newPlayer

        nextFreeId++

        return newPlayer
    }

    fun removePlayer(playerId : Int)
    {
        if (playerId in playerMap.keys)
            playerMap.remove(playerId)
        for (i in playerMap.values)
        {
            if (i.getTargetId() == playerId)
                i.setTarget(null)
        }
    }

    fun getPositions(id : Int) : Pair<Double, Double>?
    {
        if (id in playerMap.keys)
            return Pair(playerMap[id]!!.getX(), playerMap[id]!!.getY())
        else
            return null
    }

    fun setAngle(id : Int, angle : Double)
    {
        playerMap[id]!!.setMovement(angle, playerMap[id]!!.getSpeed())
    }

    fun tick()
    {
        for (i in playerMap.keys)
        {
            val player = playerMap[i]!!
            var playerX = player.getX()
            var playerY = player.getY()
            playerX += player.getSpeed() * cos(player.getAngle())
            playerY += player.getSpeed() * sin(player.getAngle())

            playerX = border(playerX, 0.0, maxX)
            playerY = border(playerY, 0.0, maxY)

            playerMap[i]!!.setPosition(playerX, playerY)

            if (playerX == maxX || playerX == 0.0)
            {
                player.setMovement(PI - player.getAngle(), player.getSpeed())
            }

            if (playerY == maxX || playerY == 0.0)
            {
                player.setMovement(2*PI - player.getAngle(), player.getSpeed())
            }

        }

        for (i in playerMap.keys)
        {
            //println("Checking collision for $i, target IS ${playerMap[i]!!.getTargetId()}")
            val player = playerMap[i]!!
            if (player.getTargetId() != null)
            {
                if (playerMap[player.getTargetId()!!] != null) {
                    val target = playerMap[player.getTargetId()!!]!!
                    val tX = target.getX()
                    val tY = target.getY()
                    val pX = player.getX()
                    val pY = player.getY()

                    //println("${player.getId()}: {$pX; $pY} -> ${target.getId()}: {$tX; $tY")

                    if (sqrt((tX - pX) * (tX - pX) + (tY - pY) * (tY - pY)) <= 2 * radius)
                    {
                        //println("${player.getId()} COLLIDE WITH ${target.getId()}")
                        var newTarget = getNewTarget(player.getId()!!)

                        //println("${player!!.getId()} is chasing $newTarget")

                        var newX = Random.nextDouble(maxX)
                        var newY = Random.nextDouble(maxY)
                        playerMap[i] = Player(player.getId()!!, newTarget, newX, newY)
                    }
                }
                else
                {
                    player.setTarget(null)
                }
            }
        }
    }
}