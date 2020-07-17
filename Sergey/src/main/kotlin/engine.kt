import kotlin.math.PI
import kotlin.random.Random

class Engine {
    private val width = 800.0
    private val height = 600.0
    private val radius = 20.0
    var numOfIds: Int = 0
    var players = mutableListOf<Player>()

    fun findPl(id: Int): Player? =
        players.find { player: Player ->
            player.id == id
    }
    data class Player(val id: Int){
        var x: Double = 0.0
        var y: Double = 0.0
        var speedX: Double = 20.0
        var speedY: Double = 0.0
        val speed: Double = 20.0
        var direction: Double = 0.0
        var isTarget = 0
        var haveTarget = -1
        var points = 0.0
    }
    fun addPlayer() {
        val x = Random.nextDouble(radius, width-radius)
        val y = Random.nextDouble(radius, height-radius)
        val player = Player(numOfIds++)
        player.x = x
        player.y = y
        players.add(player)
    }
    fun changeDir(id: Int, lr: Double){
        val player = findPl(id)
        if (player != null) {
            player.direction = (lr+player.direction)/2.0
            if(players[id].direction > 2* PI) players[id].direction -= 2 * PI
            if(players[id].direction < 2* PI) players[id].direction += 2 * PI
            player.speedX = (player.speed - player.points*0.4) * kotlin.math.sin(player.direction)
            player.speedY = -(player.speed - player.points*0.4) * kotlin.math.cos(player.direction)
        }
    }
    fun movePlayers(){
        for (player in players){
            if((player.x > width-radius) && (player.speedX > 0)){
                player.speedX = -player.speedX
            }
            if((player.x < radius) && (player.speedX < 0)){
                player.speedX = -player.speedX
            }
            if((player.y > height-radius) && (player.speedY > 0)){
                player.speedY = -player.speedY
            }
            if((player.y < radius) && (player.speedY < 0)){
                player.speedY = -player.speedY
            }
            player.x += player.speedX
            player.y += player.speedY
        }
    }
    fun updatePlayerState(id: Int, mul: Double){
        val pl = findPl(id)!!
        pl.x = Random.nextDouble(radius, width-radius)
        pl.y = Random.nextDouble(radius, height-radius)
        pl.points += 1.0*mul
        if (mul < 0.0) pl.isTarget = 0
        changeDir(pl.id, Random.nextDouble(2*PI))
    }
}
fun main(){
}