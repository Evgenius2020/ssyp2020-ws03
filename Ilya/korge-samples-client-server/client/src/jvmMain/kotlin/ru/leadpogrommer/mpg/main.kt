package ru.leadpogrommer.mpg

import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.SizeInt
import kotlin.reflect.KClass

suspend fun main() = Korge(Korge.Config(Module))

object Module: Module(){
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
