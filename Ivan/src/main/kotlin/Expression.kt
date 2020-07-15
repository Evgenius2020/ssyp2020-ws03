import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

sealed class Expression{
    abstract fun getVal(): Double
}

class Const(private val value:Double): Expression() {
    override fun getVal(): Double = value
}

sealed class SingleArgumentOperation(protected val expr: Expression): Expression()

class Inc(expr: Expression): SingleArgumentOperation(expr) {
    override fun getVal(): Double = expr.getVal() + 1
}

class Dec(expr: Expression): SingleArgumentOperation(expr) {
    override fun getVal(): Double = expr.getVal() - 1
}

class Sqrt(expr: Expression): SingleArgumentOperation(expr) {
    override fun getVal(): Double = sqrt(expr.getVal())
}

class Sqr(expr: Expression): SingleArgumentOperation(expr) {
    override fun getVal(): Double{
        val tmp = expr.getVal()
        return tmp * tmp
    }
}

sealed class TwoArgumentOperation(protected val lhs: Expression, protected val rhs: Expression): Expression()

class Sum(lhs: Expression, rhs: Expression): TwoArgumentOperation(lhs, rhs){
    override fun getVal(): Double = lhs.getVal() + rhs.getVal()
}

class Sub(lhs: Expression, rhs: Expression): TwoArgumentOperation(lhs, rhs){
    override fun getVal(): Double = lhs.getVal() - rhs.getVal()
}

class Mul(lhs: Expression, rhs: Expression): TwoArgumentOperation(lhs, rhs){
    override fun getVal(): Double = lhs.getVal() * rhs.getVal()
}

class Div(lhs: Expression, rhs: Expression): TwoArgumentOperation(lhs, rhs){
    override fun getVal(): Double = lhs.getVal() / rhs.getVal()
}

class Pow(lhs: Expression, rhs: Expression): TwoArgumentOperation(lhs, rhs){
    override fun getVal(): Double = lhs.getVal().pow(rhs.getVal())
}

sealed class ManyArgumentOperation(args: List<Expression>): Expression(){
    protected val vals = args.map { expr -> expr.getVal() }
}


class Max(args: List<Expression>): ManyArgumentOperation(args){
    override fun getVal(): Double = vals.max() ?: throw IllegalArgumentException("No args")
}

class Min(args: List<Expression>): ManyArgumentOperation(args){
    override fun getVal(): Double = vals.min() ?: throw IllegalArgumentException("No args")
}

class Avg(args: List<Expression>): ManyArgumentOperation(args){
    override fun getVal(): Double{
        val tmp = vals.average()
        if(tmp.isNaN()){
            throw IllegalArgumentException("No args")
        }
        return tmp
    }
}
