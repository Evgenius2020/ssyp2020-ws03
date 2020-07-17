import java.lang.Math.random
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.*

class BorovEngine(private val fieldw: Double, private val fieldh: Double) {
    private val players = HashMap<String, BorovPlayer>();

    fun tick() {
        for(player in players.values) {
            player.tick()

            /* Prevent going outside the map */
            if(player.x > fieldw) {
                player.x = fieldw;
                player.dir += PI;
            } else if(player.y < 0.0) {
                player.x = 0.0;
                player.dir += PI;
            }
            if(player.y > fieldw) {
                player.y = fieldh;
                player.dir += PI;
            } else if(player.y < 0.0) {
                player.y = 0.0;
                player.dir += PI;
            }

            /* Player-Target collision */
            val targetid = player.target
            if(targetid != null) {
                if(players.containsKey(targetid)) {
                    val target = players[targetid];
                    val dist = sqrt((target!!.x - player.x).pow(2.0) + (target.y - player.y).pow(2.0))
                    if(dist - (player.hitradius + target.hitradius) < 1) {
                        findTarget(player)
                    }
                } else findTarget(player)
            } else findTarget(player)
        }
    }

    private fun findTarget(player: BorovPlayer) {
        val candidates = players.values.filter { it.uuid != player.uuid && it.target != player.uuid }.filter { for(lplayer in players.values) if(lplayer.target == it.uuid) false; true; }
        if(candidates.isEmpty()) player.target = null;
        else {
            player.target = candidates.random().uuid;
            randomTeleport(player)
        }
    }
    private fun randomTeleport(player: BorovPlayer) {
        player.x = random() * fieldw
        player.y = random() * fieldh
    }

    fun registerPlayer(): BorovPlayer {
        val uuid = UUID.randomUUID().toString()
        val player = BorovPlayer(uuid)
        randomTeleport(player)
        findTarget(player)
        players[uuid] = player
        return player
    }
    fun unregisterPlayer(player: BorovPlayer) = unregisterPlayer(player.uuid)
    fun unregisterPlayer(uuid: String) = if(players.containsKey(uuid)) { players.remove(uuid); true } else { false }

    fun getMap() = players.values.toTypedArray()
    fun setPlayerDirection(player: BorovPlayer, direction: Double) = setPlayerDirection(player.uuid, direction)
    fun setPlayerDirection(uuid: String, direction: Double) = if(players.containsKey(uuid)) { players[uuid]!!.dir = direction; true } else { false }

    fun getPlayer(uuid: String) = players[uuid]

    fun debug() {
        for(player in players.values) player.debug();
    }
}

class BorovPlayer(val uuid: String) {
    var x = 0.0
    var y = 0.0
    var dir = 0.0
    var vel = 1.0
    val hitradius = 5.0

    var target: String? = null

    fun tick() {
        x += vel * cos(dir)
        y += vel * sin(dir)
    }

    fun debug() {
        println("$uuid ($x $y) > $target");
    }
}