import com.soywiz.korlibs.samples.clientserver.Action
import kotlinx.serialization.*

@Serializable
class Request(val a: Action, val args: Array<@ContextualSerialization Any>)