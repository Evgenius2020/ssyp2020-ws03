package shared

data class RenderInfo (val entities : Array<Entity>,
                       val teamsMap: HashMap<Int, Int>,
                       val shootCooldown: Double,
                       val endGameTimer: Int) : java.io.Serializable