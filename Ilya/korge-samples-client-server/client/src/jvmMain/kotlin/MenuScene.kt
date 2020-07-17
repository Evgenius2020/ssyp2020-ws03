import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.textButton
import com.soywiz.korge.service.process.*
import com.soywiz.korge.view.*

class MenuScene() : Scene() {
    suspend override fun Container.sceneInit() {
        textButton{
            text = "Start"
            onClick {
                sceneContainer.changeTo<GameScene>("127.0.0.1", 1337)
            }
            centerOn(stage!!.root)
        }

        textButton {
            text = "Exit"
            onClick {
                NativeProcess(views).close()
            }
            alignBottomToBottomOf(stage!!.root, 10.0)
            alignLeftToLeftOf(stage!!.root, 10.0)
        }
    }
}
