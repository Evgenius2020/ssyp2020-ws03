import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.PI

class ParameterTest
{
    @Test
    fun parameterTest()
    {
        Parameters.addParameter("n", 7.0)
        var var1 = Const(PI)
        var var2 = Const("k")
        var1 = Const(Executor.execute(Operators.SUM, var1, Const(22.0))!!)
        Parameters.addParameter("k", 29.0)
        assertEquals(3.858407, Executor.execute(Operators.SUB, var2, var1)!!, 1e-6)
        var1 = Const("n")
        assertEquals(22.0, Executor.execute(Operators.SUB, var2, var1)!!, 1e-6)
    }
}