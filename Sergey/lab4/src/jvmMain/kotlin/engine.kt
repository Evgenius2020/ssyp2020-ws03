import com.soywiz.klock.seconds
import com.soywiz.korge.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.Cancellable
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.interpolation.Easing
import kotlin.math.PI
import kotlin.random.Random
const val width = 800.0
const val height = 600.0
const val radiusc = 20.0

class Engine {
    var numOfIds: Int = 0
    var players = mutableListOf<Player>()

    fun findPl(id: Int): Player? =
            players.find { player: Player ->
                player.id == id
            }
    data class Player(val id: Int){
        var point = Point(0.0, 0.0)
        var speedX: Double = 0.0
        var speedY: Double = 20.0
        val speed: Double = 20.0
        var direction: Double = 0.0
        var isTarget = 0
        var haveTarget: Int? = null
        var points = 0.0
    }
    fun addPlayer() {
        val player = Player(numOfIds++)
        player.point.x = Random.nextDouble(radiusc, width-radiusc)
        player.point.y = Random.nextDouble(radiusc, height-radiusc)
        players.add(player)
    }
    fun changeDir(id: Int, lr: Double){
        players[id].direction = lr
        if(players[id].direction > 2* PI) players[id].direction -= 2 * PI
        if(players[id].direction < 2* PI) players[id].direction += 2 * PI
        players[id].speedX = (players[id].speed - players[id].points*0.4) * kotlin.math.sin(players[id].direction)
        players[id].speedY = -(players[id].speed - players[id].points*0.4) * kotlin.math.cos(players[id].direction)
    }
    fun movePlayers(){
        for (player in players){
            val id = player.id
            if((players[id].point.x > width-radiusc) && (players[id].speedX > 0)){
                players[id].speedX = -players[id].speedX
            }
            if((player.point.x < radiusc) && (player.speedX < 0)){
                players[id].speedX = -players[id].speedX
            }
            if((player.point.y > height-radiusc) && (player.speedY > 0)){
                players[id].speedY = -players[id].speedY
            }
            if((player.point.y < radiusc) && (player.speedY < 0)){
                players[id].speedY = -players[id].speedY
            }
            players[id].point.x += players[id].speedX
            players[id].point.y += players[id].speedY
        }
    }
    fun updatePlayerState(id: Int, mul: Double){
        players[id].point.x = Random.nextDouble(radiusc, width-radiusc)
        players[id].point.y = Random.nextDouble(radiusc, height-radiusc)
        players[id].points += mul
        changeDir(id, Random.nextDouble(2*PI))
    }
}