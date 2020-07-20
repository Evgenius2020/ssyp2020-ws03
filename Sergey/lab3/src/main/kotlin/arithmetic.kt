import kotlin.math.pow
import kotlin.math.sqrt

interface Expression {
    fun getVal(): Double
}

open class Const(private val num: Double): Expression {
    override fun getVal(): Double{
        return num
    }
    var name: String? = null
    /*if (name != null){
        Parameter.addParameter(name, num)
    }*/
}

interface IsSingleArgumentOperation{
    fun apply(a: Expression): Double
}

interface IsTwoArgumentOperation{
    fun apply(a: Expression, b: Expression): Double
}

interface  IsManyArgumentOperation{
    fun apply(nums: Array<Expression>): Double
}

enum class SingleArgumentOperation: IsSingleArgumentOperation{
    Inc {
        override fun apply(a: Expression): Double = a.getVal() + 1.0
    },
    Dec {
        override fun apply(a: Expression): Double = a.getVal() - 1.0
    },
    Sqr {
        override fun apply(a: Expression): Double = a.getVal() * a.getVal()
    },
    Sqrt {
        override fun apply(a: Expression): Double = sqrt(a.getVal())
    }
}

enum class TwoArgumentOperation: IsTwoArgumentOperation{
    Sum{
        override fun apply(a: Expression, b: Expression): Double = a.getVal() + b.getVal()
    },
    Sub{
        override fun apply(a: Expression, b: Expression): Double = a.getVal() - b.getVal()
    },
    Mul{
        override fun apply(a: Expression, b: Expression): Double = a.getVal() * b.getVal()
    },
    Div{
        override fun apply(a: Expression, b: Expression): Double = a.getVal() / b.getVal()
    },
    Pow{
        override fun apply(a: Expression, b:Expression): Double = a.getVal().pow(b.getVal())
    }
}

enum class ManyArgumentOperation: IsManyArgumentOperation{
    Max{
        override fun apply(nums: Array <Expression>): Double{
            var cnt = nums[0].getVal()
            for (i in 1 until nums.size){
                if (cnt < nums[i].getVal()) cnt = nums[i].getVal()
            }
            return cnt
        }
    },
    Min{
        override fun apply(nums: Array <Expression>): Double{
            var cnt = nums[0].getVal()
            for (i in 1 until nums.size){
                if (cnt > nums[i].getVal()) cnt = nums[i].getVal()
            }
            return cnt
        }
    },
    Avg{
        override fun apply(nums: Array <Expression>): Double{
            var cnt = 0.0
            for (i in nums.indices){
                cnt += nums[i].getVal()
            }
            cnt /= nums.size
            return cnt
        }
    }
}

class SingleArgumentExpression(
    private val ac: IsSingleArgumentOperation,
    private val expression: Expression
): Expression{
    override fun getVal(): Double {
        return ac.apply(expression)
    }
    companion object Operations{
        fun inc(a: Expression): SingleArgumentExpression =
            SingleArgumentExpression(SingleArgumentOperation.Inc, a)

        fun dec(a: Expression): SingleArgumentExpression =
            SingleArgumentExpression(SingleArgumentOperation.Dec, a)

        fun sqr(a: Expression): SingleArgumentExpression =
            SingleArgumentExpression(SingleArgumentOperation.Sqr, a)

        fun sqrt(a: Expression): SingleArgumentExpression =
            SingleArgumentExpression(SingleArgumentOperation.Sqrt, a)
    }
}

class TwoArgumentExpression(
    private val ac: IsTwoArgumentOperation,
    private val e1: Expression,
    val e2: Expression
): Expression{
    override fun getVal(): Double {
        return ac.apply(e1, e2)
    }
    companion object Operations{
        fun sum(e1: Expression, e2: Expression): TwoArgumentExpression =
            TwoArgumentExpression(TwoArgumentOperation.Sum, e1, e2)

        fun sub(e1: Expression, e2: Expression): TwoArgumentExpression =
            TwoArgumentExpression(TwoArgumentOperation.Sub, e1, e2)

        fun mul(e1: Expression, e2: Expression): TwoArgumentExpression =
            TwoArgumentExpression(TwoArgumentOperation.Mul, e1, e2)

        fun div(e1: Expression, e2: Expression): TwoArgumentExpression =
            TwoArgumentExpression(TwoArgumentOperation.Div, e1, e2)

        fun pow(e1: Expression, e2: Expression): TwoArgumentExpression =
            TwoArgumentExpression(TwoArgumentOperation.Pow, e1, e2)
    }
}

class ManyArgumentExpression(
    private val ac: IsManyArgumentOperation,
    private val nums: Array <Expression>
): Expression {
    override fun getVal(): Double {
        return ac.apply(nums)
    }
    companion object Operations{
        fun max(nums: Array<Expression>): ManyArgumentExpression =
            ManyArgumentExpression(ManyArgumentOperation.Max, nums)

        fun min(nums: Array<Expression>): ManyArgumentExpression =
            ManyArgumentExpression(ManyArgumentOperation.Min, nums)

        fun avg(nums: Array<Expression>): ManyArgumentExpression =
            ManyArgumentExpression(ManyArgumentOperation.Avg, nums)
    }
}

fun main(){
    /*Parameter.addParameter("a", 6.0)
    val r = Parameter.showParameter("a")!!
    println(TwoArgumentExpression.pow(Const(2.0), Const(r)).getVal())*/
}
