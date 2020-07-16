import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

class EngineTest
{
    @Test
    fun registerTest()
    {
        var engine = Engine()
        assertEquals(0, engine.playerMap.size)

        engine.registerPlayer()
        assertEquals(1, engine.playerMap.size)

        engine.registerPlayer()
        assertEquals(2, engine.playerMap.size)

        engine.registerPlayer()
        assertEquals(3, engine.playerMap.size)

        engine.registerPlayer()
        assertEquals(4, engine.playerMap.size)

        engine.registerPlayer()
        assertEquals(5, engine.playerMap.size)

    }

    @Test
    fun chooseTargetTest()
    {
        var engine = Engine()

        engine.registerPlayer()
        assertEquals(null, engine.playerMap[0]!!.getTargetId())

        engine.registerPlayer()
        assertEquals(0, engine.playerMap[1]!!.getTargetId())

        engine.registerPlayer()
        assertEquals(1, engine.playerMap[2]!!.getTargetId())

        engine.registerPlayer()
        assertEquals(2, engine.playerMap[3]!!.getTargetId())

        engine.registerPlayer()
        assertEquals(3, engine.playerMap[4]!!.getTargetId())

    }

    @Test
    fun setAngleTest()
    {
        var engine = Engine()

        engine.registerPlayer()
        assertEquals(0.0, engine.playerMap[0]!!.getAngle())

        engine.setAngle(0, PI)

        assertEquals(PI, engine.playerMap[0]!!.getAngle())

        engine.setAngle(0, 4 * PI / 3)

        assertEquals(4 * PI / 3, engine.playerMap[0]!!.getAngle())

        engine.registerPlayer()

        assertEquals(4 * PI / 3, engine.playerMap[0]!!.getAngle())
        assertEquals(0.0, engine.playerMap[1]!!.getAngle())

        engine.setAngle(1, 7 * PI / 3)
        assertEquals(7 * PI / 3, engine.playerMap[1]!!.getAngle())
    }

    @Test
    fun getPositionsTest()
    {
        var engine = Engine()

        engine.registerPlayer()
        engine.registerPlayer()
        engine.registerPlayer()

        assertEquals(engine.playerMap[0]!!.getX(), engine.getPositions(0)!!.first)
        assertEquals(engine.playerMap[0]!!.getY(), engine.getPositions(0)!!.second)

        assertEquals(engine.playerMap[1]!!.getX(), engine.getPositions(1)!!.first)
        assertEquals(engine.playerMap[1]!!.getY(), engine.getPositions(1)!!.second)

        assertEquals(engine.playerMap[2]!!.getX(), engine.getPositions(2)!!.first)
        assertEquals(engine.playerMap[2]!!.getY(), engine.getPositions(2)!!.second)
    }

    @Test
    fun tickTest()
    {
        var engine = Engine()

        engine.registerPlayer()

        var x = engine.getPositions(0)!!.first
        var y = engine.getPositions(0)!!.second

        engine.tick()
        assertEquals(x + 1, engine.getPositions(0)!!.first)

        engine.setAngle(0, PI)
        engine.tick()
        assertEquals(x, engine.getPositions(0)!!.first)

        engine.setAngle(0, 5 * PI / 2)
        engine.tick()
        assertEquals(y + 1, engine.getPositions(0)!!.second)

        engine.setAngle(0, 7 * PI / 2)
        engine.tick()
        assertEquals(y, engine.getPositions(0)!!.second)

        engine.setAngle(0, PI / 4)

        engine.tick()
        assertEquals(border(y + sqrt(2.0)/2, 0.0, engine.maxY), engine.getPositions(0)!!.second, 1e-6)
        assertEquals(border(x + sqrt(2.0)/2, 0.0, engine.maxX), engine.getPositions(0)!!.first, 1e-6)
    }
}