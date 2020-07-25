import kotlinx.coroutines.CompletableDeferred

sealed class ServerMsg(var __type__: String = "")
class Register(val response: CompletableDeferred<Player>) : ServerMsg(Register::class.java.simpleName)
class ChangeDirection(var pId: Long? = null, val dot: Dot) : ServerMsg(ChangeDirection::class.java.simpleName)
class GetPlayers(var response: CompletableDeferred<HashMap<Long, Player>>? = null) : ServerMsg(GetPlayers::class.java.simpleName)
class Disconnect(var pId: Long) : ServerMsg(Disconnect::class.java.simpleName)
object SetTargets : ServerMsg(SetTargets::class.java.simpleName)
object Update : ServerMsg(Update::class.java.simpleName)
class Players(val players: HashMap<Long, Player>) : ServerMsg(Players::class.java.simpleName)
