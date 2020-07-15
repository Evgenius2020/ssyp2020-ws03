object Parameters
{
    private var parameters = mutableMapOf<String, Double>()

    fun addParameter(name : String, value : Double)
    {
        parameters[name] = value
    }

    fun takeParameter(name : String) : Double?
    {
        return when
        {
            parameters.containsKey(name) -> parameters[name]
            else -> null
        }
    }

}