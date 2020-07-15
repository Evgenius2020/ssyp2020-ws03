import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Tests {
    @Test
    fun testAdd() {
        assertEquals(42, Integer.sum(19, 23))
    }

    @Test
    fun testDivide() {
        assertThrows(ArithmeticException::class.java) { Integer.divideUnsigned(42, 0) }
    }
}