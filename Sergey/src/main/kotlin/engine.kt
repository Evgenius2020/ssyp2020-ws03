import kotlin.math.PI
import kotlin.random.Random

class Engine {
    fun findPl(id: Int): Player? =
        players.find { player: Player ->
            player.id == id
    }
    var numOfIds: Int = 0
    var players = mutableListOf<Player>()
    data class Player(val id: Int){
        var x: Double = 0.0
        var y: Double = 0.0
        var speedX: Double = 20.0
        var speedY: Double = 0.0
        var direction: Double = 0.0
        var isTarget: Int = 0
    }
    fun changeDir(id: Int, lr: Double){
        val player = findPl(id)
        if (player != null) {
            player.direction = (lr+player.direction)/2.0
            if(players[id].direction > 2* PI) players[id].direction -= 2* PI
            if(players[id].direction < 2* PI) players[id].direction += 2* PI
            player.speedX = 20.0 * kotlin.math.sin(player.direction)
            player.speedY = -20.0 * kotlin.math.cos(player.direction)
        }
    }
    fun movePlayers(){
        for (player in players){
            if(player.x >= 790){
                player.speedX = -player.speedX
            }
            if(player.x <= 10){
                player.speedX = -player.speedX
            }
            if(player.y >= 590){
                player.speedY = -player.speedY
            }
            if(player.x <= 10){
                player.speedY = -player.speedY
            }
            player.x += player.speedX
            player.y += player.speedY
        }
    }
    fun addPlayer() {
        val x = Random.nextInt(800).toDouble()
        val y = Random.nextInt(600).toDouble()
        val player = Player(numOfIds++)
        player.x = x
        player.y = y
        players.add(player)
    }
    fun updatePlayerState(id: Int, mul: Double){
        val pl = players[id]
        pl.x = Random.nextInt(800).toDouble()
        pl.y = Random.nextInt(600).toDouble()
        pl.direction = Random.nextInt(6283).toDouble()/1000.0
        pl.speedX -= mul
        pl.speedY += mul
    }
}
fun main(){
}