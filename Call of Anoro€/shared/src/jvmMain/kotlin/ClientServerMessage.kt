package shared

import Statistic

sealed class ClientServerMessage : java.io.Serializable
class Register(val nick: String) : ClientServerMessage()
object GetRenderInfo : ClientServerMessage()
class SetAngle (val point : ClientServerPoint) : ClientServerMessage()
object Shoot : ClientServerMessage()
class ChangeSpeed(val x: Int, val y: Int) : ClientServerMessage()
object GetStatistic : ClientServerMessage()