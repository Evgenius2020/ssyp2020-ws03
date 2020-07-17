import com.soywiz.klock.seconds
import com.soywiz.korge.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.interpolation.Easing
import kotlinx.coroutines.delay
import java.awt.Color
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.random.Random

suspend fun main() = Korge(width = 640, height = 640, bgcolor = Colors["#2b2b2b"]) {
	val engine = Engine()
	val radius = 10.0
	val circleList = mutableListOf<Pair<Circle, Text>>()
	for (i in 0..3)
	{
		val player = engine.registerPlayer()
		player.setMovement(Random.nextDouble(-PI, PI), player.getSpeed())
		val circle = circle (radius, Colors.ORANGE).xy(player.getX()-radius, player.getY()-radius)
		val text = text(player.getId().toString(), 18.0).xy(player.getX() - radius + 1.0, player.getY() - radius + 1.0)
		circleList.add(Pair(circle, text))

		println("Player {${player.getId()}} target is {${player.getTargetId()}}")

	}

	while (true)
	{
		engine.tick()
		for (i in 0..3)
		{
			val pos = engine.getPositions(i)
			circleList[i].first.xy(pos!!.first - radius, pos.second - radius)
			circleList[i].second.xy(pos.first - radius + 1.0, pos.second- radius + 1.0)
		}

		for (i in 0..3)
		{
			val targetId = engine.playerMap[i]!!.getTargetId()
			if (targetId != null)
			{
				val pp = engine.getPositions(i)!!
				val tp = engine.getPositions(targetId)!!

				val newAngle = atan2(tp.second - pp.second, tp.first - pp.first)

				engine.playerMap[i]!!.setMovement(newAngle, engine.playerMap[i]!!.getSpeed())
			}
			else
			{
				val newTarget = engine.getNewTarget(i)
				engine.playerMap[i]!!.setTarget(newTarget)
				println("Player {$i} target is {$newTarget}")
			}
		}
		delay(5)
	}

	//for (i in engine.playerMap.keys)
	//{
//
	//}

	//val circle = circle (20.0, Colors.GOLD).xy(300.0, 300.0)
	//circle.addUpdater {
	//	radius++
	//	x--
	//	y--
	//}
}