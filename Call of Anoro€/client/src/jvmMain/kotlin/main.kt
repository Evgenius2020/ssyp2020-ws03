package client

import client.scenes.*
import com.soywiz.korge.*
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.ScaleMode
import com.soywiz.korma.geom.SizeInt
import client.util.LoadingProxyScene
import kotlin.reflect.KClass

suspend fun main(args: Array<String>){
    if(args.size > 0){
        ClientConfiguration.server = args[0]
    }
    Korge(Korge.Config(module = MainModule))
}

object MainModule : Module() {

    override val mainScene: KClass<out Scene>
        get() = MainScene::class
    override val title: String
        get() = "Call of Anoro€"
    override val windowSize: SizeInt
        get() = SizeInt(640, 640)
    override val size: SizeInt
        get() = SizeInt(640, 640)

    override suspend fun AsyncInjector.configure() {
        mapPrototype { MainScene() }
        mapPrototype { GameScene(get()) }
        mapPrototype { LoadingProxyScene(get(), get()) }
    }
}