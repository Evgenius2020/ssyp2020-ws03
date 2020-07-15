import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*


class Junit5Test{
    @Test
    fun testSingle(){
        assertEquals(SingleArgumentExpression.inc(Const(1456.0)).getVal(), 1457.0, 0.00001)
        assertEquals(SingleArgumentExpression.dec(Const(3.1415)).getVal(), 2.1415, 0.00001)
        assertEquals(SingleArgumentExpression.sqr(Const(37.0)).getVal(), 1369.0, 0.00001)
        assertEquals(SingleArgumentExpression.sqrt(Const(66.0)).getVal(), 8.124038, 0.00001)
    }
    @Test
    fun testTwo(){
        assertEquals(TwoArgumentExpression.sum(Const(3.1415),
            Const(2.7182)).getVal(), 5.8597, 0.00001)

        assertEquals(TwoArgumentExpression.sub(Const(3.1415),
            Const(2.7182)).getVal(), 0.4233, 0.00001)

        assertEquals(TwoArgumentExpression.mul(Const(3.1),
            Const(200.7)).getVal(), 622.17, 0.00001)

        assertEquals(TwoArgumentExpression.div(Const(3.0),
            Const(2.5)).getVal(), 1.2, 0.00001)

        assertEquals(TwoArgumentExpression.pow(Const(3.0),
            Const(5.0)).getVal(), 243.0, 0.00001)
    }
    @Test
    fun testMany(){
        val arr = arrayOf<Expression>(Const(1.0), Const(4.9), Const(5.0), Const(3.0))
        assertEquals(ManyArgumentExpression.max(arr).getVal(), 5.0, 0.00001)
        assertEquals(ManyArgumentExpression.min(arr).getVal(), 1.0, 0.00001)
        assertEquals(ManyArgumentExpression.avg(arr).getVal(), 3.475, 0.00001)
    }
}