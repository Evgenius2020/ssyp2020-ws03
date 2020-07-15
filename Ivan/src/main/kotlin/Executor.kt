import java.lang.IllegalArgumentException

object Executor {
    fun execute(expression: Expression): Double?{
        return try{
            expression.getVal()
        } catch (exc: IllegalArgumentException) {
            null
        }
    }
}