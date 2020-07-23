package client.scenes

import client.util.LoadingProxyScene
import com.soywiz.klock.seconds
import com.soywiz.korge.input.mouse
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs

class MainScene : Scene() {
    private lateinit var bg: View
    private lateinit var title: View
    private lateinit var button: View

    override suspend fun Container.sceneInit() {
        bg = solidRect(views.virtualWidth, views.virtualHeight, Colors.SANDYBROWN) {
            position(0.0, 0.0)
        }

        title = image(resourcesVfs["title.png"].readBitmap()) {
            anchor(0.5, 0.5)
            position(views.virtualWidth / 2, views.virtualHeight / 4)
        }

        button = image(resourcesVfs["start.png"].readBitmap()) {
            anchor(0.5, 0.5)
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
                    if (mouseEnabled) {
                        mouseEnabled = false
                        scale -= 0.5
                        sceneContainer.changeTo<LoadingProxyScene>(LoadingProxyScene.NextScreen(GameScene::class), time = .5.seconds)
                    }
                }
            }
        }
    }
}