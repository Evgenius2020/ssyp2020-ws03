import kotlinx.coroutines.CompletableDeferred

sealed class BorovMessage()
class BorovMessageRegister(val callback: CompletableDeferred<String>): BorovMessage()
class BorovMessageMap(val callback: CompletableDeferred<HashMap<String, BorovPlayer>>): BorovMessage()
class BorovMessageDirection(val id: String, val angle: Double, val callback: CompletableDeferred<Boolean>): BorovMessage()
class BorovMessageInfo(val id: String, val callback: CompletableDeferred<BorovPlayer?>): BorovMessage()