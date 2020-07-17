import com.soywiz.kds.Pool
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.seconds
import com.soywiz.korau.sound.NativeSoundChannel
import com.soywiz.korau.sound.readMusic
import com.soywiz.korev.Key
import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.readBitmap
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korio.async.delay
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.SizeInt
import com.soywiz.korma.interpolation.Easing
//import com.soywiz.korma.geom.ro
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.*

import kotlin.random.Random
import kotlin.reflect.KClass

//import com.soywiz.korge.*

suspend fun main(args: Array<String>) = Korge(Korge.Config(module))

object module: Module(){
    override val bgcolor: RGBA
        get() = Colors.BLACK
    override val icon: String?
        get() = "coronavirus.png"
    override val size: SizeInt
        get() = SizeInt(640, 480)
    override val mainScene: KClass<out Scene>
        get() = MenuScene::class

    override suspend fun AsyncInjector.configure(){
        mapPrototype { MenuScene() }
        mapPrototype { GameScene(get(), get()) }
    }
}
