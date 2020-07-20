import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.Math.random
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.*

class BorovEngine(private val fieldw: Double, private val fieldh: Double) {
    private val players = HashMap<String, BorovPlayer>();
    private val playerMutex = Mutex()

    suspend fun tick() {
        for(player in players.values) {
            player.tick()

            /* Prevent going outside the map */
            if(player.x + player.hitradius > fieldw) {
                player.x = player.x + player.hitradius;
                player.dir = PI;
            } else if(player.x - player.hitradius < 0.0) {
                player.x = player.hitradius;
                player.dir = 0.0;
            }
            if(player.y + player.hitradius > fieldh) {
                player.y = fieldh - player.hitradius;
                player.dir = -PI / 2;
            } else if(player.y - player.hitradius < 0.0) {
                player.y = player.hitradius;
                player.dir = PI / 2;
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

    private suspend fun findTarget(player: BorovPlayer) {
        playerMutex.withLock {
            val candidates = players.values.filter { it.uuid != player.uuid && it.target != player.uuid }.filter {
                !players.values.map { it.target }.contains(it.uuid)
            }
            if(candidates.isEmpty()) player.target = null;
            else {
                player.target = candidates.random().uuid;
                randomTeleport(player)
            }
        }
    }
    private fun randomTeleport(player: BorovPlayer) {
        player.x = random() * fieldw
        player.y = random() * fieldh
    }

    suspend fun registerPlayer(): BorovPlayer {
        val uuid = UUID.randomUUID().toString()
        val player = BorovPlayer(uuid)
        randomTeleport(player)
        findTarget(player)
        players[uuid] = player
        return player
    }
    fun unregisterPlayer(player: BorovPlayer) = unregisterPlayer(player.uuid)
    fun unregisterPlayer(uuid: String) = if(players.containsKey(uuid)) { players.remove(uuid); true } else { false }

    fun getMap() = players // TODO: Implement deep copy to prevent player access from outside
    fun setPlayerDirection(player: BorovPlayer, direction: Double) = setPlayerDirection(player.uuid, direction)
    fun setPlayerDirection(uuid: String, direction: Double) = if(players.containsKey(uuid)) { players[uuid]!!.dir = direction; true } else { false }

    fun getPlayer(uuid: String) = players[uuid]

    fun debug() {
        for(player in players.values) player.debug();
    }
}