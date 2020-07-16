class State {
    private val entities = mutableMapOf<Long, Entity>()
    private var nextID = 1L

    fun getEntity(id: Long) = entities[id]

    fun getIterator() = entities.iterator()

    fun getSt() = entities.entries

    fun registerEntity(e: Entity){
        e.id = nextID
        nextID++
        entities[e.id] = e
    }

    fun deleteEntity(e: Entity){
        entities.remove(e.id)
    }
}