import java.lang.Exception

object Executor {
    fun execute(exp: Expression) = try { exp.getVal() } catch (error: Exception) { null }
}