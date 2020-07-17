import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.lang.Exception

class ExpressionsTest {
    @Test
    fun test() {
        assertEquals(1337.0, Const(1337.0).getVal())
        assertEquals(1338.0, Inc(Const(1337.0)).getVal())
        assertEquals(1336.0, Dec(Const(1337.0)).getVal())
        assertEquals(2500.0, Sqr(Const(50.0)).getVal())
        assertEquals(50.0, Sqrt(Const(2500.0)).getVal())

        assertEquals(1337.0, Sum(Const(1268.0), Const(69.0)).getVal())
        assertEquals(177013.0, Sub(Const(178501.0), Const(1488.0)).getVal())
        assertEquals(230053.0, Mul(Const(607.0), Const(379.0)).getVal())
        assertEquals(228922.0, Div(Const(15108852.0), Const(66.0)).getVal())
        assertEquals(46483360000.0, Pow(Const(215600.0), Const(2.0)).getVal())

        assertEquals(777.0, Max(Const(777.0), Const(13.0), Const(200.0)).getVal())
        assertEquals(13.0, Min(Const(777.0), Const(13.0), Const(200.0)).getVal())
        assertEquals(330.0, Avg(Const(777.0), Const(13.0), Const(200.0)).getVal())

        val varmgr = VariableManager()
        varmgr.set("a", 228.0)
        varmgr.set("b", 666.0)

        assertEquals(228.0, Const("a", varmgr).getVal())
        assertEquals(894.0, Sum(Const("a", varmgr), Const("b", varmgr)).getVal())
        assertThrows<Exception> { Sum(Const("a", varmgr), Const("c", varmgr)).getVal() }

        assertEquals(894.0, Executor.execute(Sum(Const("a", varmgr), Const("b", varmgr))))
        assertNull(Executor.execute(Sum(Const("a", varmgr), Const("c", varmgr))))
    }
}