package shared

import engine.PlayerInfo

data class RenderInfo (val entities : Array<Entity>, val info : MutableMap<Int, PlayerInfo>) : java.io.Serializable