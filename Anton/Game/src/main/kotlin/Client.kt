import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.acos
import kotlin.math.sqrt

val mutex = Mutex()

class Client ()
{
    private lateinit var player : Player

    fun start()
    {
        player = Server.registerPlayer()
        GlobalScope.launch {
            if (player.getTargetId() == null)
            {
                mutex.withLock {
                    var id = player.getId()
                    player.setTarget(Server.getNewTarget(id!!))
                }
                delay(1000)
            }
            if (player.getTargetId() != null)
            {
                var angle : Double
                var pP = Server.getPosition(player.getId()!!) // first - X, second - Y
                var tP = Server.getTargetPosition(player.getId()!!)!! // first - X, second - Y
                angle = ((pP.first * tP.first) + (pP.first * tP.first)) /
                        (sqrt(pP.first * pP.first + pP.second * pP.second) * sqrt(tP.first * tP.first + tP.second * tP.second)) // угол между векторами
                angle = acos(angle)
                mutex.withLock {
                    Server.setAngle(player.getId()!!, angle)
                }
                delay(1000)
            }
            else
            {
                delay(1000)
            }
        }

    }
}