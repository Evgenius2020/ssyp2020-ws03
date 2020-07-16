import kotlin.math.abs
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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
    var maxX = 640.0
    var maxY = 640.0
    val radius = 5.0

    private fun getNewTarget(playerId : Int) : Int?
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
        var x = ThreadLocalRandom.current().nextDouble(0.0, 640.0)
        var y = ThreadLocalRandom.current().nextDouble(0.0, 640.0)

        var newPlayer = Player(nextFreeId, getNewTarget(nextFreeId), x, y)

        playerMap[nextFreeId] = newPlayer

        nextFreeId++

        return newPlayer
    }

    fun getPositions(id : Int) : Pair<Double, Double>?
    {
        if (id < nextFreeId)
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
        }

        for (i in playerMap.keys)
        {

            val player = playerMap[i]!!
            if (player.getTargetId() != null)
            {
                val target = playerMap[player.getTargetId()!!]!!

                var tX = target.getX()
                var tY = target.getY()
                var pX = player.getX()
                var pY = player.getY()

                if (sqrt((tX - pX) * (tX - pX) + (tY - pY) * (tY - pY)) <= 2 * radius) {
                    var newTarget = getNewTarget(player.getId()!!)
                    if (newTarget != null)
                        if (newTarget == player.getTargetId())
                            newTarget = null
                    playerMap[i] = Player(

                        player.getId(),
                        newTarget,
                        ThreadLocalRandom.current().nextDouble(0.0, 640.0),
                        ThreadLocalRandom.current().nextDouble(0.0, 640.0)
                    )
                }
            }
        }

    }
}