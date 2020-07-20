package server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun CoroutineScope.serverActor() = actor<ServerMsg> {
    val s = ServerActions()

    for (msg in channel) {
        if (!isClosedForReceive) {
            when (msg) {
                is Register -> s.register(msg.u)
                is Tick -> s.tick()
                is GetRenderInfo -> s.getRenderInfo(msg.e, msg.res)
                is SetAngle -> s.setAngle(msg.e, msg.point)
                is Shoot -> s.shoot(msg.e)
                is Disconnect -> s.disconnect(msg.e)
            }
        }
    }
}