package engine.managers

import engine.Configuration
import shared.Bullet
import shared.Entity
import shared.Object
import shared.Player

class DamageManagerData(
        var team: Int = -1
)

class DamageManager: BaseManager<DamageManagerData>(){
    var friendlyFire: Boolean? = null

    fun register(entity: Entity, team: Int){
        super.register(entity, DamageManagerData())
        entitiesData[entity]!!.team = team
    }
    fun remove(entity: Entity){
        super.delete(entity)
    }

    fun processCollisions(arr: Array<Pair<Entity, Entity>>?): Array<Player>?{
        val arrDead = mutableListOf<Player>()
        if (arr != null) {
            for ((ent1, ent2) in arr){
                if ((ent1 !is Object) && (ent2 !is Object)){
                    if (friendlyFire == true){
                        if ((ent1 is Bullet) && (ent2 is Player)){
                            ent2.health -= ent1.damage
                            remove(ent1)
                        }
                        if ((ent1 is Player) && (ent2 is Bullet)){
                            ent1.health -= ent2.damage
                            remove(ent2)
                        }
                    }
                    if (friendlyFire == false){
                        if ((ent1 is Bullet) && (ent2 is Player) && (ent1.team != ent2.team)){
                            ent2.health -= ent1.damage
                            remove(ent1)
                        }
                        if ((ent1 is Player) && (ent2 is Bullet) && (ent1.team != ent2.team)){
                            ent1.health -= ent2.damage
                            remove(ent2)
                        }
                    }
                }
            }
            for (ent in entitiesData.keys){
                if ((ent is Player) && (ent.health <= 0)){
                    arrDead.add(ent)
                }
            }
            return arrDead.toTypedArray()
        }
        else return null
    }
}