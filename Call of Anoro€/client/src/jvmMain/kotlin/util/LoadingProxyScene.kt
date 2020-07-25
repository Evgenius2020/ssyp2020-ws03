package client.util

import com.soywiz.klock.seconds
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.reflect.KClass

class LoadingProxyScene(nextScreen: NextScreen, val nick : String) : Scene() {

    private lateinit var loadingText: Text
    private lateinit var bg: View
    private val text: String = "Loading..."
    private val nextScreen: KClass<*> = nextScreen.nextScreenClass

    override suspend fun Container.sceneInit() {
        bg = TiledMapView(resourcesVfs["menuBg.tmx"].readTiledMap())
        addChild(bg)

        loadingText = text(text) {
            textSize = 14.0
            x = (views.virtualHeight - this.width).toDouble()
            y = (views.virtualHeight - 20).toDouble()
            filtering = false
        }
    }

    override suspend fun Container.sceneMain() {
        sceneContainer.changeTo(clazz = nextScreen as KClass<Scene>, injects = *arrayOf(nick), time = .5.seconds)
    }

    data class NextScreen(val nextScreenClass: KClass<*>)

}