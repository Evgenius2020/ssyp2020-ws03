import java.lang.StrictMath.pow
import kotlin.math.sqrt

abstract class Expression ()
{
    abstract fun getValue() : Double
}

class Const (private val number : Any) : Expression()
{
    override fun getValue() : Double
    {
        when (number)
        {
            is Double -> return (number)
            is String ->
            {
                if (Parameters.takeParameter(number) == null)
                {
                    throw IllegalArgumentException("Parameter is not defined")
                }
                else
                    return Parameters.takeParameter(number)!!
            }
            else -> throw IllegalArgumentException("Variable is not defined")
        }
    }
}

abstract class SingleArgumentOperation() : Expression()
{
    abstract override fun getValue() : Double
}

class Increment(private val argument : Expression) : SingleArgumentOperation()
{
    override fun getValue() : Double
    {
        return argument.getValue() + 1
    }
}

class Decrement(private val argument : Expression) : SingleArgumentOperation()
{
    override fun getValue() : Double
    {
        return argument.getValue() - 1
    }
}

class Square(private val argument : Expression) : SingleArgumentOperation()
{
    override fun getValue() : Double
    {
        return argument.getValue() * argument.getValue()
    }
}

class SquareRoot(private val argument : Expression) : SingleArgumentOperation()
{
    override fun getValue() : Double
    {
        return sqrt(argument.getValue())
    }
}

abstract class TwoArgumentOperation()
{
    abstract fun getValue() : Double
}

class Sum(private val argumentL : Expression, private val argumentR : Expression) : TwoArgumentOperation()
{
    override fun getValue() : Double
    {
        return argumentL.getValue() + argumentR.getValue()
    }
}

class Sub(private val argumentL : Expression, private val argumentR : Expression) : TwoArgumentOperation()
{
    override fun getValue() : Double
    {
        return argumentL.getValue() - argumentR.getValue()
    }
}

class Mul(private val argumentL : Expression, private val argumentR : Expression) : TwoArgumentOperation()
{
    override fun getValue() : Double
    {
        return argumentL.getValue() * argumentR.getValue()
    }
}

class Div(private val argumentL : Expression, private val argumentR : Expression) : TwoArgumentOperation()
{
    override fun getValue() : Double
    {
        return argumentL.getValue() / argumentR.getValue()
    }
}

class Pow(private val argumentL : Expression, private val argumentR : Expression) : TwoArgumentOperation()
{
    override fun getValue() : Double
    {
        return pow(argumentL.getValue(), argumentR.getValue())
    }
}

abstract class ManyArgumentOperation()
{
    abstract fun getValue() : Double
}

class Max(vararg args : Expression ) : ManyArgumentOperation()
{
    private val arguments = args

    override fun getValue(): Double
    {
        var max : Double = arguments[0].getValue()
        for (i in arguments)
            if (i.getValue() > max)
                max = i.getValue()
        return max
    }
}

class Min(vararg args : Expression ) : ManyArgumentOperation()
{
    private val arguments = args

    override fun getValue(): Double
    {
        var min : Double = arguments[0].getValue()
        for (i in arguments)
            if (i.getValue() < min)
                min = i.getValue()
        return min
    }
}

class Avg(vararg args : Expression ) : ManyArgumentOperation()
{
    private val arguments = args

    override fun getValue(): Double
    {
        var avg : Double = 0.0
        for (i in arguments)
            avg += i.getValue()
        return avg / arguments.size
    }
}

fun main()
{

}