import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import Operators
import kotlin.math.sqrt

class ExecutorTest
{
    @Test
    fun executorTest()
    {
        var var1 = Const(7.0)

        var var2 = Executor.execute(Operators.INC, var1)!!
        assertEquals(8.0, var2.getValue(), 1e-6)

        var var3 = Executor.execute(Operators.MAX, var1, var2, Const(9.9))!!
        assertEquals(9.9, var3.getValue(), 1e-6)

        var3 = Executor.execute(Operators.SQRT, var3)!!
        assertEquals(3.14642654451, var3.getValue(), 1e-6)

        var3 = Executor.execute(Operators.MUL, var3, var2, var1)!!

        assertEquals(25.1714123561, var3.getValue(), 1e-6)
    }
}