package server

import shared.*

sealed class ServerRequest : java.io.Serializable
class GetMapRequest (val result : MutableMap<Int, Player>) : ServerRequest()
class SetAngleRequest (val playerId : Int, val newAngle : Double) : ServerRequest()
class GetNewTargetRequest (val playerId : Int, val result : Int?) : ServerRequest()