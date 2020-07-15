import java.lang.StrictMath.pow
import kotlin.math.sqrt
import Operators
import Executor
import Parameters

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
            is Double -> return (number as Double)
            is String ->
            {
                if (Parameters.takeParameter(number as String) == null)
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

abstract class SingleArgumentOperation()
{
    abstract fun doAction() : Const
}

class Increment(private val argument : Const) : SingleArgumentOperation()
{
    override fun doAction() : Const
    {
        return Const(argument.getValue() + 1)
    }
}

class Decrement(private val argument : Const) : SingleArgumentOperation()
{
    override fun doAction() : Const
    {
        return Const(argument.getValue() - 1)
    }
}

class Square(private val argument : Const) : SingleArgumentOperation()
{
    override fun doAction() : Const
    {
        return Const(argument.getValue() * argument.getValue())
    }
}

class SquareRoot(private val argument : Const) : SingleArgumentOperation()
{
    override fun doAction() : Const
    {
        return Const(sqrt(argument.getValue()))
    }
}

abstract class TwoArgumentOperation()
{
    abstract fun doAction() : Const
}

class Sum(private val argumentL : Const, private val argumentR : Const) : TwoArgumentOperation()
{
    override fun doAction() : Const
    {
        return Const(argumentL.getValue() + argumentR.getValue())
    }
}

class Sub(private val argumentL : Const, private val argumentR : Const) : TwoArgumentOperation()
{
    override fun doAction() : Const
    {
        return Const(argumentL.getValue() - argumentR.getValue())
    }
}

class Mul(private val argumentL : Const, private val argumentR : Const) : TwoArgumentOperation()
{
    override fun doAction() : Const
    {
        return Const(argumentL.getValue() * argumentR.getValue())
    }
}

class Div(private val argumentL : Const, private val argumentR : Const) : TwoArgumentOperation()
{
    override fun doAction() : Const
    {
        return Const(argumentL.getValue() / argumentR.getValue())
    }
}

class Pow(private val argumentL : Const, private val argumentR : Const) : TwoArgumentOperation()
{
    override fun doAction() : Const
    {
        return Const(pow(argumentL.getValue(), argumentR.getValue()))
    }
}

abstract class ManyArgumentOperation()
{
    abstract fun doAction() : Const
}

class Max(vararg args : Const ) : ManyArgumentOperation()
{
    private val arguments = args

    override fun doAction(): Const
    {
        var max : Double = arguments[0].getValue()
        for (i in arguments)
            if (i.getValue() > max)
                max = i.getValue()
        return Const(max)
    }
}

class Min(vararg args : Const ) : ManyArgumentOperation()
{
    private val arguments = args

    override fun doAction(): Const
    {
        var min : Double = arguments[0].getValue()
        for (i in arguments)
            if (i.getValue() < min)
                min = i.getValue()
        return Const(min)
    }
}

class Avg(vararg args : Const ) : ManyArgumentOperation()
{
    private val arguments = args

    override fun doAction(): Const
    {
        var avg : Double = 0.0
        for (i in arguments)
            avg += i.getValue()
        return Const(avg / arguments.size)
    }
}

fun main()
{
    var variable = Const("a")
    Parameters.addParameter("a", 5.0)

    variable = Executor.execute(Operators.SUM, variable, Const(5.0))!!

    print(variable.getValue())

}