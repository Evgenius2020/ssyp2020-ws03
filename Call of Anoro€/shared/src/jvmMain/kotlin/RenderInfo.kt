package shared

data class RenderInfo (val entities : Array<Entity>,
                       val teamsMap: HashMap<Int, Int>,
                       val shootCooldown: Double,
                       val endGameTimer: Int,
                       val stopTimer: Int,
                       val respawnTimer: Int,
                       val isDead: Boolean,
                       val pId: Int) : java.io.Serializable