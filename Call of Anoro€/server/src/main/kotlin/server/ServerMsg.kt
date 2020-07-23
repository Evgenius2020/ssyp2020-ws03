package server

import kotlinx.coroutines.CompletableDeferred
import shared.*

sealed class ServerMsg
class Register(val u: CompletableDeferred<Player>): ServerMsg()
object Tick : ServerMsg()
class GetRenderInfo(val p: Player, val res: CompletableDeferred<RenderInfo>): ServerMsg()
class SetAngle(val e: Entity, val point: ClientServerPoint): ServerMsg()
class Shoot(val p: Player): ServerMsg()
class Disconnect(val p: Player): ServerMsg()
class ChangeSpeed(val m: Moveable, val speedX: Double?, val speedY: Double?): ServerMsg()