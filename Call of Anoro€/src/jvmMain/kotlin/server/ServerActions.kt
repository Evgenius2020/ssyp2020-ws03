package server

import engine.Engine
import shared.Entity
import kotlinx.coroutines.CompletableDeferred
import shared.ClientServerPoint
import shared.RenderInfo
import java.lang.Math.atan2

class ServerActions {

    val eng = Engine()

    fun register(res: CompletableDeferred<Entity>){
        res.complete(eng.registerPlayer())
    }

    fun tick(){
        eng.tick()
    }

    fun getRenderInfo(e: Entity, res: CompletableDeferred<RenderInfo>){
        res.complete(RenderInfo(eng.getEntities(e), eng.getPlayerInfos()))
    }

    fun setAngle(e: Entity, point: ClientServerPoint) {
        eng.setAngle(e, atan2(point.y - e.y, point.x - e.x))
    }

    fun shoot(e: Entity) {
        eng.shot(e)
    }
}
