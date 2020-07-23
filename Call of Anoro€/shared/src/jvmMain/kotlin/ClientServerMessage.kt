package shared

sealed class ClientServerMessage : java.io.Serializable
//object Register : ClientServerMessage()
object GetRenderInfo : ClientServerMessage()
class SetAngle (val point : ClientServerPoint) : ClientServerMessage()
object Shoot : ClientServerMessage()
class ChangeSpeed(val x: Int, val y: Int) : ClientServerMessage()