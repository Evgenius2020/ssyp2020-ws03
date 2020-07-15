import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.E
import kotlin.math.PI

class MathTest
{
    @DisplayName ("Increment")
    @Test
    fun incrementTest()
    {
        assertEquals(6.0, Increment(Const(5.0)).doAction().getValue(), 1e-6)
        assertEquals(-8.0, Increment(Const(-9.0)).doAction().getValue(), 1e-6)
        assertEquals(13.3, Increment(Const(12.3)).doAction().getValue(), 1e-6)
        assertEquals(1.0, Increment(Const(0.0)).doAction().getValue(), 1e-6)
        assertEquals(-41.89, Increment(Const(-42.89)).doAction().getValue(), 1e-6)
    }

    @DisplayName ("Decrement")
    @Test
    fun decrementTest()
    {
        assertEquals(4.0, Decrement(Const(5.0)).doAction().getValue(), 1e-6)
        assertEquals(-10.0, Decrement(Const(-9.0)).doAction().getValue(), 1e-6)
        assertEquals(11.3, Decrement(Const(12.3)).doAction().getValue(), 1e-6)
        assertEquals(-1.0, Decrement(Const(0.0)).doAction().getValue(), 1e-6)
        assertEquals(-43.89, Decrement(Const(-42.89)).doAction().getValue(), 1e-6)
    }

    @DisplayName ("Square")
    @Test
    fun squareTest()
    {
        assertEquals(25.0, Square(Const(5.0)).doAction().getValue(), 1e-6)
        assertEquals(81.0, Square(Const(-9.0)).doAction().getValue(), 1e-6)
        assertEquals(151.29, Square(Const(12.3)).doAction().getValue(), 1e-6)
        assertEquals(0.0, Square(Const(0.0)).doAction().getValue(), 1e-6)
        assertEquals(1839.5521, Square(Const(-42.89)).doAction().getValue(), 1e-6)
    }

    @DisplayName ("SquareRoot")
    @Test
    fun squareRootTest()
    {
        assertEquals(2.236068, SquareRoot(Const(5.0)).doAction().getValue(), 1e-6)
        assertEquals(Double.NaN, SquareRoot(Const(-9.0)).doAction().getValue(), 1e-6)
        assertEquals(3.507136, SquareRoot(Const(12.3)).doAction().getValue(), 1e-6)
        assertEquals(0.0, SquareRoot(Const(0.0)).doAction().getValue(), 1e-6)
        assertEquals(Double.NaN, SquareRoot(Const(-42.89)).doAction().getValue(), 1e-6)
    }

    @DisplayName ("Sum")
    @Test
    fun sumTest()
    {
        assertEquals(7.718282, Sum(Const(5.0), Const(E)).doAction().getValue(), 1e-6)
        assertEquals(-6.281718, Sum(Const(-9.0), Const(E)).doAction().getValue(), 1e-6)
        assertEquals(15.018282, Sum(Const(12.3), Const(E)).doAction().getValue(), 1e-6)
        assertEquals(E, Sum(Const(0.0), Const(E)).doAction().getValue(), 1e-6)
        assertEquals(-40.171718, Sum(Const(-42.89), Const(E)).doAction().getValue(), 1e-6)
    }

    @DisplayName ("Sub")
    @Test
    fun subTest()
    {
        assertEquals(5.0-E, Sub(Const(5.0), Const(E)).doAction().getValue(), 1e-6)
        assertEquals(-9.0-E, Sub(Const(-9.0), Const(E)).doAction().getValue(), 1e-6)
        assertEquals(12.3-E, Sub(Const(12.3), Const(E)).doAction().getValue(), 1e-6)
        assertEquals(-E, Sub(Const(0.0), Const(E)).doAction().getValue(), 1e-6)
        assertEquals(-42.89-E, Sub(Const(-42.89), Const(E)).doAction().getValue(), 1e-6)
    }

    @DisplayName ("Mul")
    @Test
    fun mulTest()
    {
        assertEquals(0.0, Mul(Const(5.0), Const(0.0)).doAction().getValue(), 1e-6)
        assertEquals(9.0, Mul(Const(-9.0), Const(-1.0)).doAction().getValue(), 1e-6)
        assertEquals(86.1, Mul(Const(12.3), Const(7.0)).doAction().getValue(), 1e-6)
        assertEquals(0.0, Mul(Const(0.0), Const(E)).doAction().getValue(), 1e-6)
        assertEquals(-842.7885, Mul(Const(-42.89), Const(19.65)).doAction().getValue(), 1e-6)
    }

    @DisplayName ("Div")
    @Test
    fun divTest()
    {
        assertEquals(0.0, Div(Const(0.0), Const(5.0)).doAction().getValue(), 1e-6)
        assertEquals(9.0, Div(Const(-9.0), Const(-1.0)).doAction().getValue(), 1e-6)
        assertEquals(1.757143, Div(Const(12.3), Const(7.0)).doAction().getValue(), 1e-6)
        assertEquals(0.0, Div(Const(0.0), Const(E)).doAction().getValue(), 1e-6)
        assertEquals(3.140845, Div(Const(223.0), Const(71.0)).doAction().getValue(), 1e-6)
    }

    @DisplayName ("Pow")
    @Test
    fun powTest()
    {
        assertEquals(1.0, Pow(Const(0.0), Const(0.0)).doAction().getValue(), 1e-6)
        assertEquals(0.25, Pow(Const(2.0), Const(-2.0)).doAction().getValue(), 1e-6)
        assertEquals(16.0, Pow(Const(4.0), Const(2.0)).doAction().getValue(), 1e-6)
        assertEquals(499907.747562, Pow(Const(4.1), Const(9.3)).doAction().getValue(), 1e-6)
        assertEquals(23.140693, Pow(Const(E), Const(PI)).doAction().getValue(), 1e-6)
    }

    @DisplayName ("Max")
    @Test
    fun maxTest()
    {
        assertEquals(0.00001, Max(Const(0.0), Const(0.0), Const(0.00001)).doAction().getValue())
        assertEquals(2.0, Max(Const(2.0), Const(-2.0)).doAction().getValue())
        assertEquals(7.3, Max(Const(4.0), Const(2.0), Const(7.3)).doAction().getValue())
        assertEquals(-4.1, Max(Const(-4.1), Const(-9.3)).doAction().getValue())
        assertEquals(PI, Max(Const(E), Const(PI)).doAction().getValue())
    }

    @DisplayName ("Min")
    @Test
    fun minTest()
    {
        assertEquals(0.0, Min(Const(0.0), Const(0.0), Const(0.00001)).doAction().getValue())
        assertEquals(-2.0, Min(Const(2.0), Const(-2.0)).doAction().getValue())
        assertEquals(2.0, Min(Const(4.0), Const(2.0), Const(7.3)).doAction().getValue())
        assertEquals(-9.3, Min(Const(-4.1), Const(-9.3)).doAction().getValue())
        assertEquals(E, Min(Const(E), Const(PI)).doAction().getValue())
    }

    @DisplayName ("Avg")
    @Test
    fun avgTest()
    {
        assertEquals(0.00001/3, Avg(Const(0.0), Const(0.0), Const(0.00001)).doAction().getValue(), 1e-6)
        assertEquals(0.0, Avg(Const(2.0), Const(-2.0)).doAction().getValue(), 1e-6)
        assertEquals(13.3/3, Avg(Const(4.0), Const(2.0), Const(7.3)).doAction().getValue(), 1e-6)
        assertEquals(-6.7, Avg(Const(-4.1), Const(-9.3)).doAction().getValue(), 1e-6)
        assertEquals(2.929937, Avg(Const(E), Const(PI)).doAction().getValue(), 1e-6)
        assertEquals(Double.NaN, Avg().doAction().getValue(), 1e-6)
    }

}