import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import kotlin.math.PI

class Task3Test {
    @Test
    fun test() {
        val engine = BorovEngine(1000.0, 1000.0)

        var player1 = engine.registerPlayer()
        var player2 = engine.registerPlayer()
        assertFalse(player1.target == null && player2.target == null)

        val x1 = player1.x
        val y1 = player1.y
        engine.tick()
        val x2 = player1.x
        val y2 = player1.y
        assertFalse(x1 == x2 && y1 == y2)

        player1.x = 99999.0
        player1.y = 99999.0
        player2.x = -90.0
        player2.y = -90.0
        engine.tick()
        assertNotEquals(99999.0, player1.x)
        assertNotEquals(99999.0, player1.y)
        assertNotEquals(-90.0, player2.x)
        assertNotEquals(-90.0, player2.y)

        val catcher = if(player1.target == null) player2 else player1
        assertNotNull(catcher)

        player1.x = 100.0
        player1.y = 100.0
        player1.dir = 0.0
        player2.x = 100.0
        player2.y = 110.0
        player2.dir = PI
        engine.tick()

        assertNotEquals(catcher, 100.0)
    }
}