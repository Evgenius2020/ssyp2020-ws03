import com.soywiz.korau.sound.NativeSound
import com.soywiz.korau.sound.NativeSoundChannel
import com.soywiz.korau.sound.readMusic
import com.soywiz.korau.sound.readSound
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.SceneContainer
import com.soywiz.korge.service.process.NativeProcess
import com.soywiz.korge.ui.textButton
import com.soywiz.korge.view.*
import com.soywiz.korim.color.RGBA
import com.soywiz.korio.file.std.resourcesVfs

class GameOverOverlay(val scc: SceneContainer): FixedSizeContainer(scc.views.virtualWidth.toDouble(), scc.views.virtualHeight.toDouble()) {
    lateinit var sfxw: NativeSound
    lateinit var sfxl: NativeSound
    lateinit var sfx: NativeSoundChannel
    lateinit var msg: Text
    suspend fun load(){




        sfxl = resourcesVfs[GameResult.LOOSE.audio].readSound()
        sfxw = resourcesVfs[GameResult.WIN.audio].readSound()

    }
    fun activate(res: GameResult){
        x  = 0.0
        y = 0.0

        solidRect(this.width, this.height, RGBA(100, 100, 100, 100))
        msg = text(res.text){
            centerOn(this@GameOverOverlay)
        }

        textButton {
            text = "Restart"
            alignTopToBottomOf(msg)
            centerXOn(this@GameOverOverlay)
            onClick {
                this@GameOverOverlay.removeFromParent()
                sfx.stop()
                scc.changeTo<GameScene>()
            }
        }
        textButton {
            text = "Exit"
            alignLeftToLeftOf(this@GameOverOverlay)
            alignBottomToBottomOf(this@GameOverOverlay)
            onClick { NativeProcess(scc.views).close() }
        }

//        println(sfxl.length.seconds)
//        sfx = sfxl.play()
        sfx = (if (res == GameResult.WIN)sfxw else sfxl).play()
    }


}