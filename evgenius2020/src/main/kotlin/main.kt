interface Expression {
    fun getVal(): Double
}

class Const(private val a: Double) : Expression {
    override fun getVal(): Double {
        return this.a
    }
}

interface ISingleArgumentAction {
    fun apply(arg: Expression): Double
}

enum class SingleArgumentAction : ISingleArgumentAction {
    Inc {
        override fun apply(arg: Expression): Double {
            return arg.getVal() + 1;
        }
    },
    Dec {
        override fun apply(arg: Expression): Double {
            return arg.getVal() - 1;
        }

    }
}

class SingleArgumentExpression(
    private var action: ISingleArgumentAction,
    private var arg: Expression
) : Expression {
    override fun getVal(): Double {
        return action.apply(arg)
    }

    companion object {
        fun inc(arg: Expression): SingleArgumentExpression =
            SingleArgumentExpression(SingleArgumentAction.Inc, arg)
    }
}

fun main() {
    val expr = SingleArgumentExpression.inc(Const(4.0))
    print(expr.getVal())
}