package client.scenes

import client.util.LoadingProxyScene
import com.soywiz.klock.seconds
import com.soywiz.korev.Key
import com.soywiz.korev.keys
import com.soywiz.korge.input.mouse
import com.soywiz.korge.input.onKeyTyped
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.reflect.typeOf

class MainScene : Scene() {
    private lateinit var bg: View
    private lateinit var title: View
    private lateinit var button: View
    private lateinit var nickBox: View
    private var nick = ""
    private lateinit var nickText : View
    val maxNickSize = 9
    val minNickSize = 1

    override suspend fun Container.sceneInit() {
        bg = TiledMapView(resourcesVfs["menuBg.tmx"].readTiledMap())
        addChild(bg)

        title = image(resourcesVfs["title.png"].readBitmap()) {
            anchor(0.5, 0.5)
            position(views.virtualWidth / 2, views.virtualHeight / 4)
            smoothing = false
        }

        nickBox = image(resourcesVfs["nickBox.png"].readBitmap()) {
            smoothing = false
            anchor(0.5, 0.5)
            height = height * 3 / 4
            width = width * 3 / 4
            position(views.virtualWidth / 2, views.virtualHeight * 2 / 4)
            addUpdater {

            }
            views.keys {
                onKeyTyped {
                    keyEvent ->
                    run {
                        //print(keyEvent.character == '\b')
                        when
                        {
                            keyEvent.character =='\b' -> {
                                if (nick.length > 0)
                                    nick = nick.substring(0 until nick.length - 1)
                            }
                            nick.length == maxNickSize -> { }
                            keyEvent.character == '\n' -> { }
                            keyEvent.character == ' ' -> { }

                            else -> nick += keyEvent.character
                        }
                    }
                }
            }
        }

        nickText = text(nick, 35.0, Colors.BLACK) {
            filtering = false
            addUpdater {
                text = nick
                alignLeftToLeftOf(nickBox)
                alignTopToTopOf(nickBox)
                x -= nickBox.width * 3 / 8 - 10
                y -= nickBox.height * 3 / 8 - 10
            }
        }

        button = image(resourcesVfs["start.png"].readBitmap()) {
            smoothing = false
            anchor(0.5, 0.5)
            scale(0.5, 0.5)
            position(views.virtualWidth / 2, views.virtualHeight * 3 / 4)
            tint = Colors.LIGHTGRAY
            mouse {
                over {
                    tint = Colors.WHITE
                }
                out {
                    tint = Colors.LIGHTGRAY
                }
                onClick {
                    if (nick.length >= minNickSize && nick.length <= maxNickSize) {
                        if (mouseEnabled) {
                            mouseEnabled = false
                            scale -= 0.5
                            sceneContainer.changeTo<LoadingProxyScene>(LoadingProxyScene.NextScreen(GameScene::class), nick,  time = .5.seconds)
                        }
                    }
                }
            }
        }
    }
}