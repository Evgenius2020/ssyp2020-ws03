import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ContextualSerialization

class State {
    val m = Mutex()


    private val entities = mutableMapOf<Long, Entity>()
    private var nextID = 1L

    suspend fun getEntity(id: Long)= entities[id]!!

    fun getIterator() = entities.iterator()

    fun getSt() = entities as Map<@ContextualSerialization Long, @ContextualSerialization Entity>


    suspend fun registerEntity(e: Entity){
        e.id = nextID
        nextID++
        entities[e.id] = e
    }

    suspend fun deleteEntity(e: Entity) {
        entities.remove(e.id)
    }
}