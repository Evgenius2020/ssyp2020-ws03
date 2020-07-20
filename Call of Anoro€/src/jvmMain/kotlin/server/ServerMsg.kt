package server

import engine.Entity
import kotlinx.coroutines.CompletableDeferred
import shared.ClientServerPoint
import shared.RenderInfo

sealed class ServerMsg
class Register(val u: CompletableDeferred<Entity>): ServerMsg()
object Tick : ServerMsg()
class GetRenderInfo(val e: Entity, val res: CompletableDeferred<RenderInfo>): ServerMsg()
class SetAngle(val e: Entity, val point: ClientServerPoint): ServerMsg()
class Shoot(val e: Entity): ServerMsg()
class Disconnect(val e: Entity): ServerMsg()