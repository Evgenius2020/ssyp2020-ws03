import com.soywiz.korma.geom.Point
//import kotlinx.serialization.ContextualSerialization
//import kotlinx.serialization.Serializable

//@Serializable
open class Entity {
    var id = 0L

//    @ContextualSerialization
    var pos = Point(0, 0)

//    @ContextualSerialization
    var vel = Point(0, 0)
}