import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import kotlin.math.sqrt


class ExecutorTester{
    @Test
    @DisplayName("ExecutorTest")
    fun executorTest(){
        val one = Const(1.0)
        val two = Const(2.0)
        val zero = Const(0.0)

        val sum = Sum(one, two)
        val divByTwo = Div(sum, two)
        val divByZero = Div(sum, zero)

        val listEmpty = listOf<Expression>()
        val maxEmpty = Max(listEmpty)

        val list = listOf<Expression>(one, two, sum)
        val max = Max(list)

        assertEquals(3.0, Executor.execute(sum))
        assertEquals(1.5, Executor.execute(divByTwo))
        assertEquals(Double.POSITIVE_INFINITY, Executor.execute(divByZero))
        assertEquals(null, Executor.execute(maxEmpty))
        assertEquals(3.0, Executor.execute(max))
    }
}
