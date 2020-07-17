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
    val maxX = Config.maxX
    val maxY = Config.maxY
    val radius = Config.radius

    fun getNewTarget(playerId : Int) : Int?
    {
        val chosenTargets = mutableListOf<Int>()

        for (i in playerMap.keys)
        {
            if (playerMap[i]!!.getTargetId() != null)
                chosenTargets.add(playerMap[i]!!.getTargetId()!!)

        }

        var i = 0

        while ((i < nextFreeId) && (i in chosenTargets || i == playerId || playerMap[i]!!.getTargetId() == playerId))
            i++
        if (i in playerMap.map {it.value.getId()})
            return i
        return null
    }

    fun registerPlayer() : Player
    {
        val x = Random.nextDouble(maxX.toDouble())
        val y = Random.nextDouble(maxY.toDouble())

        val newPlayer = Player(nextFreeId, getNewTarget(nextFreeId), x, y)

        playerMap[nextFreeId] = newPlayer

        nextFreeId++

        return newPlayer
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

            playerX = border(playerX, 0.0, maxX.toDouble())
            playerY = border(playerY, 0.0, maxY.toDouble())

            playerMap[i]!!.setPosition(playerX, playerY)

            if (playerX == maxX.toDouble() || playerX == 0.0)
            {
                player.setMovement(PI - player.getAngle(), player.getSpeed())
            }

            if (playerY == maxX.toDouble() || playerY == 0.0)
            {
                player.setMovement(2*PI - player.getAngle(), player.getSpeed())
            }

        }

        for (i in playerMap.keys)
        {

            val player = playerMap[i]!!
            if (player.getTargetId() != null)
            {
                val target = playerMap[player.getTargetId()!!]!!

                val tX = target.getX()
                val tY = target.getY()
                val pX = player.getX()
                val pY = player.getY()

                if (sqrt((tX - pX) * (tX - pX) + (tY - pY) * (tY - pY)) <= 2 * radius) {
                    var newTarget = getNewTarget(player.getId()!!)
                    if (newTarget != null)
                        if (newTarget == player.getTargetId())
                            newTarget = null
                    playerMap[i]!!.setTarget(newTarget)
                    playerMap[i]!!.setPosition(Random.nextDouble(maxX.toDouble()), Random.nextDouble(maxY.toDouble()))
                    playerMap[i]!!.setMovement(playerMap[i]!!.getAngle(), playerMap[i]!!.getSpeed() - 0.2)
                    target.setMovement(target.getAngle(), target.getSpeed() + 0.2)
                    println("Player {$i} target is {$newTarget}")
                }
            }
        }

    }
}