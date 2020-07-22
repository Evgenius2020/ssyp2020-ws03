package server

import shared.Entity
import kotlinx.coroutines.CompletableDeferred
import shared.ClientServerPoint
import shared.Player
import shared.RenderInfo

sealed class ServerMsg
class Register(val u: CompletableDeferred<Player>): ServerMsg()
object Tick : ServerMsg()
class GetRenderInfo(val e: Entity, val res: CompletableDeferred<RenderInfo>): ServerMsg()
class SetAngle(val e: Entity, val point: ClientServerPoint): ServerMsg()
class Shoot(val p: Player): ServerMsg()
class Disconnect(val p: Player): ServerMsg()