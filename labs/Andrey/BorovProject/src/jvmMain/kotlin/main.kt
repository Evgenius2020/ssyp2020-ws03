import com.soywiz.klock.seconds
import com.soywiz.korev.mouse
import com.soywiz.korge.*
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.ARGB
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.interpolation.Easing
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Color
import kotlin.math.PI

suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {
	val engine = BorovServer()
	var circles = HashMap<String, Circle>()
	var players: HashMap<String, BorovPlayer>

	BorovBot(engine)
	BorovBot(engine)
	BorovBot(engine)
	BorovBot(engine)
	BorovBot(engine)
	BorovBot(engine)
	BorovBot(engine)
	BorovBot(engine)
	BorovBot(engine)
	BorovBot(engine)

	val cb = CompletableDeferred<HashMap<String, BorovPlayer>>()
	engine.channel.send(BorovMessageMap(cb))
	players = cb.await()

	mouse {
		onClick {
			// Dump players and their targets
			for(player in players.values) {
				println("${player.uuid} > ${player.target}")
			}
			println()
		}
	}

	while(true) {
		val cb = CompletableDeferred<HashMap<String, BorovPlayer>>()
		engine.channel.send(BorovMessageMap(cb))
		players = cb.await()

		for(id in players.keys) {
			if(!circles.containsKey(id)) {
				circles[id] = Circle(players[id]!!.hitradius, Colors.ORANGE).xy(players[id]!!.x, players[id]!!.y)
				addChild(circles[id]!!)
			} else {
				circles[id]!!.xy(players[id]!!.x, players[id]!!.y)
			}
		}
		circles.keys.filter { !players.keys.contains(it) }.forEach { circles.remove(it) }

		delay(10)
	}
}