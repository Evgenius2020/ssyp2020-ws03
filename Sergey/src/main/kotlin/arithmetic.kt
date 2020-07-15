interface Expression {
    fun getVal(): Double
}
class Const(val num: Double): Expression {
    override fun getVal(): Double{
        return num
    }
}
interface isSingleArgumentOperation{
    fun apply(a: Expression): Double
}
enum class SingleArgumentOperation: isSingleArgumentOperation{
    fun Inc(val a: Double): Expression{
        override fun apply()
    }
}
