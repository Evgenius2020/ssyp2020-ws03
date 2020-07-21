package engine.managers

import engine.Configuration
import engine.Entity

class DamageManagerData(
        var health: Int = Configuration.healthOfPlayer,
        val damage: Int = Configuration.baseDamage,
        var team: Int = -1
)

class DamageManager: BaseManager<DamageManagerData>(){
    var friendlyFire: Boolean? = null
    private val listOfIsBullet = mutableMapOf<Int, Boolean>()

    fun register(entity: Entity, team: Int, isBullet: Boolean){
        super.register(entity, DamageManagerData())
        entitiesData[entity]!!.team = team
        listOfIsBullet[entity.id] = isBullet
    }
    fun remove(entity: Entity){
        super.delete(entity)
        listOfIsBullet.remove(entity.id)
    }

    fun processCollisions(arr: Array<Pair<Entity, Entity>>?): Array<Entity>?{
        var cnt = 0
        val arrDead = arrayOf<Entity>()
        if (arr != null) {
            for ((ent1, ent2) in arr){
                if (friendlyFire!!){
                    if ((listOfIsBullet[ent1.id]!!) && !(listOfIsBullet[ent2.id]!!)){
                        entitiesData[ent2]!!.health -= entitiesData[ent1]!!.damage
                    }
                    if (!(listOfIsBullet[ent1.id]!!) && (listOfIsBullet[ent2.id]!!)){
                        entitiesData[ent1]!!.health -= entitiesData[ent2]!!.damage
                    }
                }
                if (!friendlyFire!!){
                    if ((listOfIsBullet[ent1.id]!!) && !(listOfIsBullet[ent2.id]!!) &&
                            (entitiesData[ent1]!!.team != entitiesData[ent2]!!.team)){
                        entitiesData[ent2]!!.health -= entitiesData[ent1]!!.damage
                    }
                    if (!(listOfIsBullet[ent1.id]!!) && (listOfIsBullet[ent2.id]!!) &&
                            (entitiesData[ent1]!!.team != entitiesData[ent2]!!.team)){
                        entitiesData[ent1]!!.health -= entitiesData[ent2]!!.damage
                    }
                }
            }
            for (ent in entitiesData.keys){
                if (entitiesData[ent]!!.health <= 0){
                    arrDead[cnt++] = ent
                }
            }
            return arrDead
        }
        else return null
    }
}