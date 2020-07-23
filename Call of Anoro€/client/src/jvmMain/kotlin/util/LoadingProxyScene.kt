package client.util

import com.soywiz.klock.seconds
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import kotlin.reflect.KClass

class LoadingProxyScene(nextScreen: NextScreen) : Scene() {

    private lateinit var loadingText: Text
    private lateinit var bg: View
    private val text: String = "Loading..."
    private val nextScreen: KClass<*> = nextScreen.nextScreenClass


    override suspend fun Container.sceneInit() {
        bg = solidRect(views.virtualWidth, views.virtualHeight, Colors.SANDYBROWN) {
            position(0.0, 0.0)
        }

        loadingText = text(text) {
            textSize = 14.0
            position(views.virtualWidth / 2, views.virtualHeight / 2)
        }

    }

    override suspend fun Container.sceneMain() {
        sceneContainer.changeTo(clazz = nextScreen as KClass<Scene>, time = .5.seconds)
    }

    data class NextScreen(val nextScreenClass: KClass<*>)

}