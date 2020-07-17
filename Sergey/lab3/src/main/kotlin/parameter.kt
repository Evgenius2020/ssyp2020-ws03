object Parameter {
    var listOfParameters = mutableMapOf<String, Double>()
    fun addParameter(name: String, value: Double){
        listOfParameters[name] = value
    }
    fun showParameter(name: String): Double?{
        return when{
            listOfParameters.containsKey(name) -> listOfParameters[name]
            else -> null
        }
    }
}
