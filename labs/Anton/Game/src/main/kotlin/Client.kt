import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.channels.*
import kotlin.math.atan2

val mutex = Mutex()

class Client (private val serverActor : SendChannel<ServerMsg>)
{
    private var playerId : Int = -1
    private var targetId : Int? = null
    suspend fun start()
    {
        val responseRegister = CompletableDeferred<Player>()
        serverActor.send(Register(responseRegister))
        val answer = responseRegister.await()
        playerId = answer.getId()!!
        targetId = answer.getTargetId()
        if (playerId != -1) {

            println("Player is registered with id: $playerId ") // Debug

            while (true) {
                if (targetId == null)
                {
                    val responseNewTarget = CompletableDeferred<Int?>()
                    serverActor.send(GetNewTarget(playerId, responseNewTarget))
                    targetId = responseNewTarget.await()
                    println("New target for player with id: $playerId is $targetId")
                    delay(1000)
                }
                else
                {

                    val responseTargetPos = CompletableDeferred<Pair<Double, Double>?>()
                    serverActor.send(GetPositionById(targetId!!, responseTargetPos))
                    val targetPos = responseTargetPos.await()!!

                    val responsePlayerPos = CompletableDeferred<Pair<Double, Double>?>()
                    serverActor.send(GetPositionById(playerId, responsePlayerPos))
                    val playerPos = responsePlayerPos.await()!!

                    println("Player ($playerId): {$playerPos}, target ($targetId): {$targetPos}")

                    val angle = atan2(targetPos.second - playerPos.second, targetPos.first - playerPos.first)

                    serverActor.send(SetAngle(playerId, angle))
                    println("Player ($playerId) new angle is $angle")

                }
            }
        }
        else
        {
            println("Registration failed")
        }
    }
}