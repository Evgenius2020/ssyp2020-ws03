package engine.managers

import shared.Bullet
import shared.Entity
import shared.Object
import shared.Player

class DamageManagerData(
        var team: Int = -1
)

class DamageManager : BaseManager<DamageManagerData>() {
    var friendlyFire: Boolean? = null

    var upScore = mutableListOf<Int>()
    var downScore = mutableListOf<Int>()

    fun register(entity: Entity, team: Int) {
        super.register(entity, DamageManagerData())
        entitiesData[entity]!!.team = team
    }

    private fun remove(entity: Entity) {
        super.delete(entity)
    }

    fun makeDMG(player: Player, bullet: Bullet) {
        player.health -= bullet.damage
        if (player.health < 0) {
            player.deaths++
            bullet.source.kills++
            if (friendlyFire!! && player.team != bullet.team) {
                upScore.add(bullet.team)
            } else if (friendlyFire!!) {
                downScore.add(bullet.team)
            } else {
                upScore.add(bullet.team)
            }
        }
        remove(bullet)
    }

    fun processCollisions(arr: Array<Pair<Entity, Entity>>?): Array<Player>? {
        val arrDead = mutableListOf<Player>()
        if (arr != null) {
            for ((ent1, ent2) in arr) {
                if ((ent1 !is Object) && (ent2 !is Object)) {
                    if (friendlyFire == true) {
                        if ((ent1 is Bullet) && (ent2 is Player)) {
                            makeDMG(ent2, ent1)
                        }
                        if ((ent1 is Player) && (ent2 is Bullet)) {
                            makeDMG(ent1, ent2)
                        }
                    }
                    if (friendlyFire == false) {
                        if ((ent1 is Bullet) && (ent2 is Player) && (ent1.team != ent2.team)) {
                            makeDMG(ent2, ent1)
                        }
                        if ((ent1 is Player) && (ent2 is Bullet) && (ent1.team != ent2.team)) {
                            makeDMG(ent1, ent2)
                        }
                    }
                }
            }
            for (ent in entitiesData.keys) {
                if ((ent is Player) && (ent.health <= 0)) {
                    arrDead.add(ent)
                }
            }
            return arrDead.toTypedArray()
        } else return null
    }
}