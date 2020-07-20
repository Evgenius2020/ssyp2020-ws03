package shared

import engine.PlayerInfo

data class RenderInfo (val entities : Array<Entity>, val info : Array<PlayerInfo>) : java.io.Serializable