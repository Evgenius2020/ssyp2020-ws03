import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import kotlin.math.sqrt

class VariableTester{

    @Test
    @DisplayName("VariableTest")
    fun variableTest(){
        val container = VariableContainer()
        val a = Const("a", container)
        val b = Const("b", container)
        container.putVar("a", 10.0)
        container.putVar("b", 11.0)

        assertEquals(21.0, Executor.execute(Sum(a, b)))
        assertEquals(1.0, Executor.execute(Sub(b, a)))

        container.putVar("a", 1.0)
        assertEquals(12.0, Executor.execute(Sum(a, b)))

        val one = Const(1.0)
        assertEquals(12.0, Executor.execute(Sum(b, one)))
    }
}