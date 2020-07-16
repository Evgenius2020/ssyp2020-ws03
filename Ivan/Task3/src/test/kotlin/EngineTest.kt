import Engine.Dot
import Engine.Engine
import Engine.Vector
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import kotlin.math.sqrt

class EngineTester{

    @Test
    @DisplayName("CreateUserTest")
    fun createUserTest(){
        val eng = Engine

        assertEquals(0, eng.getPlayers().size)

        val p = eng.addPlayer()

        assertEquals(1, eng.getPlayers().size)
        assert(eng.getPlayers()[p.id] === p)
        eng.clear()
    }

    @Test
    @DisplayName("MoveTest")
    fun movePlayerTest(){
        val eng = Engine

        assertEquals(0, eng.getPlayers().size)

        val p = eng.addPlayer()
        p.dir = Vector(1.0, 0.0)
        p.pos = Dot(0.0, 0.0)

        eng.nextState()
        assertEquals(Dot(50.0, 0.0), p.pos)
        p.dir = Vector(-1.0, 0.0)

        eng.nextState()
        assertEquals(Dot(0.0, 0.0), p.pos)

        p.dir = Vector(1.0, 2.0)
        eng.nextState()
        assertEquals(50 / sqrt(5.0), p.pos.x, 1e-6)
        assertEquals(100 / sqrt(5.0), p.pos.y, 1e-6)
        assertEquals(50.0, Vector(p.pos).len, 1e-6)
        eng.nextState()
        eng.nextState()
        assertEquals(150.0, Vector(p.pos).len, 1e-6)
        eng.clear()
    }

    @Test
    @DisplayName("ChangeDirectionTest")
    fun changeDirectionTest(){
        val eng = Engine
        assertEquals(0, eng.getPlayers().size)

        val p = eng.addPlayer()
        p.pos = Dot(0.0, 0.0)
        p.dir = Vector(0.0, 1.0)

        eng.changeDirection(p.id, Vector(10000.0, 0.0))
        assertEquals(Vector(1.0, 0.0), p.dir)

        eng.nextState()
        assertEquals(Dot(50.0, 0.0), p.pos)
        eng.clear()
    }

    @Test
    @DisplayName("HitTest")
    fun hitTest(){
        val eng = Engine
        assertEquals(0, eng.getPlayers().size)

        val dst = eng.addPlayer()
        val src = eng.addPlayer()

        src.targetId = dst.id
        src.pos = Dot(0.0, 0.5)
        dst.pos = Dot(0.0, 0.75)

        dst.dir = Vector(0.0, 0.0)
        src.dir = Vector(0.0, 0.0)

        eng.nextState()

        assertEquals(Dot(0.0, 0.75), dst.pos)
        assertNotEquals(Dot(0.0, 0.5), src.pos)
    }
}