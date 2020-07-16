import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import kotlin.math.sqrt

class ExpressionTester{

    @Test
    @DisplayName("SingleArgumentTest")
    fun singleArgumentTest(){
        var arg0 = Const(1.0)
        assertEquals(1.0, arg0.getVal())

        assertEquals(2.0, Inc(arg0).getVal())
        assertEquals(0.0, Dec(arg0).getVal())
        assertEquals(1.0, Sqr(arg0).getVal())
        assertEquals(1.0, Sqrt(arg0).getVal())

        arg0 = Const(4.5)
        assert(sqrt(arg0.getVal()) - Sqrt(arg0).getVal() < 1e-6)
        assertEquals(20.25, Sqr(arg0).getVal())
    }

    @Test
    @DisplayName("TwoArgumnetTest")
    fun twoArgumentTest(){
        val lhs = Const(2.0)
        val rhs = Const(3.0)

        assertEquals(5.0, Sum(lhs, rhs).getVal())
        assertEquals(-1.0, Sub(lhs, rhs).getVal())
        assertEquals(6.0, Mul(lhs, rhs).getVal())
        assert((2.0 / 3.0) - Div(lhs, rhs).getVal() < 1e-6)
        assertEquals(8.0, Pow(lhs, rhs).getVal())
    }

    @Test
    @DisplayName("ManyArgumentTest")
    fun manyArgumentTest(){
        val args = listOf(Const(1.0), Const(2.0), Const(3.0))

        assertEquals(2.0, Avg(args).getVal())
        assertEquals(3.0, Max(args).getVal())
        assertEquals(1.0, Min(args).getVal())
    }

}
