import kotlinx.coroutines.channels.*
import kotlinx.coroutines.*

object Server
{
    private val engine = Engine()

    fun tick()
    {
        GlobalScope.launch {
            while (true)
            {
                engine.tick()
                delay(100)
            }
        }
    }

    fun getTargetPosition(playerId : Int) : Pair<Double, Double>?
    {
        val target = engine.playerMap[playerId]!!.getTargetId()
        if (target != null)
            return Pair(engine.playerMap[target]!!.getX(), engine.playerMap[target]!!.getY())
        else
            return null
    }

    fun getPosition(playerId : Int) : Pair<Double, Double>
    {
        return Pair(engine.playerMap[playerId]!!.getX(), engine.playerMap[playerId]!!.getY())
    }

    fun setAngle(playerId : Int, angle : Double)
    {
        engine.setAngle(playerId, angle)
    }

    fun getNewTarget(playerId : Int) : Int?
    {
        return engine.getNewTarget(playerId)
    }

    fun registerPlayer() : Player
    {
        return engine.registerPlayer()
    }

}