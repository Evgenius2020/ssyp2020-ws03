import java.lang.IllegalArgumentException

class VariableContainer {
    val base: HashMap<String, Double> = HashMap()

    fun putVar(key: String, value: Double) {
        base[key] = value
    }

    fun getVal(key: String): Double {
        return base[key] ?: throw IllegalArgumentException("There is no variable $key in VariableContainer")
    }
}