package shared

import engine.PlayerInfo

data class RenderInfo (val entities : Array<Entity>, val info : Map<Int, PlayerInfo>) : java.io.Serializable