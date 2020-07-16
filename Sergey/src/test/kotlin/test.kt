import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.lang.Math.PI


class Junit5Test{
    @Test
    fun testEngine(){
        var eng = Engine()
        eng.addPlayer()
        val oldX = eng.findPl(0)!!.x
        val oldY = eng.findPl(0)!!.y
        eng.changeDir(0, PI/3.0)
        eng.movePlayers()
        val pl = eng.findPl(0)!!
        assertEquals(oldX + 4.330127, pl.x, 1e-6)
        assertEquals(oldY - 2.5, pl.y, 1e-6)
    }
}