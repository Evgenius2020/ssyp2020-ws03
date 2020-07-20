package server

import shared.*

sealed class ServerRequest : java.io.Serializable
class GetMapRequest () : ServerRequest()
class SetAngleRequest (val playerId : Int, val newAngle : Double) : ServerRequest()
class GetNewTargetRequest (val playerId : Int) : ServerRequest()