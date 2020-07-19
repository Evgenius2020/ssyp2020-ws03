class Player constructor(private val id: Int, private var targetId: Int? = null, private var x: Double, private var y: Double) : java.io.Serializable
{

    private var angle : Double = 0.0
    private var speed : Double = 1.0

    fun getAngle() : Double = angle
    fun getSpeed() :  Double = speed
    fun getX() : Double = x
    fun getY() : Double = y
    fun getId() : Int?= id
    fun getTargetId() : Int? = targetId

    fun setMovement(newAngle : Double, newSpeed : Double)
    {
        angle = newAngle
        speed = newSpeed
    }

    fun setPosition(newX : Double, newY : Double)
    {
        x = newX
        y = newY
    }

    fun setTarget(newId : Int?)
    {
        targetId = newId
    }
}