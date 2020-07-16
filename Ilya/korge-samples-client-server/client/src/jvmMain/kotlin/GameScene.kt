import com.soywiz.korau.sound.NativeSoundChannel
import com.soywiz.korau.sound.readMusic
import com.soywiz.korev.Key
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.service.process.NativeProcess
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.scale
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.runBlockingNoSuspensions
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Point
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.random.Random

class GameScene() : Scene() {
    lateinit var music: NativeSoundChannel
    lateinit var ball: Circle
    lateinit var overlay: GameOverOverlay
    var numObstacles = 0
    lateinit var bmp: Bitmap

    var cCount = 0


    suspend override fun Container.sceneInit() {
        bmp = resourcesVfs["coronavirus.png"].readBitmap()

        text(""){
            bgcolor = Colors.BLACK
            addUpdater { text = "Rendering $cCount covids. ${1000.0 / it.milliseconds} FPS" }
        }
        for (i in 0 until 10000){
            createCOVID()
        }
    }

    val vel = Point(500, 500)
    fun createCOVID(){
        cCount++
        val covid = Image(bmp).xy(Random.nextInt(0, this.sceneView.width.toInt()), Random.nextInt(0, this.sceneView.height.toInt()))
        sceneView.addChild(covid)
        covid.addUpdater {
            x += vel.x * it.seconds
            y += vel.y * it.seconds
            val _w = sceneView.stage!!.views.virtualWidth
            val _h = sceneView.stage!!.views.virtualHeight
            if (x > _w) x =0.0
            if (y > _h) y = 0.0
        }
        covid.scale = 0.2
    }
}
