class Player constructor(id : Int? = null, targetId : Int? = null, x : Double, y : Double)
{

    private var x = x
    private var y = y
    private val id = id
    private var targetId = targetId
    private var angle : Double = 0.0
    private var speed : Double = 1.0

    fun getAngle() : Double = angle
    fun getSpeed() :  Double = speed
    fun getX() : Double = x
    fun getY() : Double = y
    fun getId() : Int? = id
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

    fun setTarget(newId : Int)
    {
        targetId = newId
    }
}