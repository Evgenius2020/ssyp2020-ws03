import kotlin.math.pow
import kotlin.math.sqrt

sealed class Expression {
    abstract fun getVal(): Double
}
sealed class SingleArgumentOperation(val exp: Expression) : Expression()
sealed class TwoArgumentOperation(val exp1: Expression, val exp2: Expression) : Expression()
sealed class ManyArgumentOperation(vararg val exp: Expression) : Expression()

class VariableManager() {
    var map: HashMap<String, Double> = HashMap()

    fun set(name: String, value: Double) = map.put(name, value)
    fun get(name: String) = map[name]
    fun has(name: String): Boolean = map.containsKey(name)
}

class Const(): Expression() {
    var sval = ""
    var dval = 0.0
    var varmgr: VariableManager? = null

    constructor(sval: String, varmgr: VariableManager): this() {
        this.sval = sval
        this.varmgr = varmgr
    }
    constructor(dval: Double): this() {
        this.dval = dval
    }

    override fun getVal(): Double {
        return if(varmgr != null) {
            if(varmgr!!.has(sval)) {
                varmgr!!.get(sval)!!
            } else {
                throw Exception("Variable not found")
            }
        } else dval
    }
}

class Inc(exp: Expression) : SingleArgumentOperation(exp) {
    override fun getVal() = exp.getVal() + 1
}
class Dec(exp: Expression) : SingleArgumentOperation(exp) {
    override fun getVal() = exp.getVal() - 1
}
class Sqr(exp: Expression) : SingleArgumentOperation(exp) {
    override fun getVal() = exp.getVal() * exp.getVal()
}
class Sqrt(exp: Expression) : SingleArgumentOperation(exp) {
    override fun getVal() = sqrt(exp.getVal())
}

class Sum(exp1: Expression, exp2: Expression) : TwoArgumentOperation(exp1, exp2) {
    override fun getVal() = exp1.getVal() + exp2.getVal()
}
class Sub(exp1: Expression, exp2: Expression) : TwoArgumentOperation(exp1, exp2) {
    override fun getVal() = exp1.getVal() - exp2.getVal()
}
class Mul(exp1: Expression, exp2: Expression) : TwoArgumentOperation(exp1, exp2) {
    override fun getVal() = exp1.getVal() * exp2.getVal()
}
class Div(exp1: Expression, exp2: Expression) : TwoArgumentOperation(exp1, exp2) {
    override fun getVal() = exp1.getVal() / exp2.getVal()
}
class Pow(exp1: Expression, exp2: Expression) : TwoArgumentOperation(exp1, exp2) {
    override fun getVal() = exp1.getVal().pow(exp2.getVal())
}

class Max(vararg exp: Expression) : ManyArgumentOperation(*exp) {
    override fun getVal(): Double {
        val result = exp.map { item -> item.getVal() }.max()
        if(result == null) throw Exception("Maximum operation error")
        else return result
    }
}
class Min(vararg exp: Expression) : ManyArgumentOperation(*exp) {
    override fun getVal(): Double {
        val result = exp.map { item -> item.getVal() }.min()
        if(result == null) throw Exception("Minimum operation error")
        else return result
    }
}
class Avg(vararg exp: Expression) : ManyArgumentOperation(*exp) {
    override fun getVal(): Double {
        val result = exp.map{ item -> item.getVal() }.average()
        return result
    }
}