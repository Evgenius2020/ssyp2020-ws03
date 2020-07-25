/*package engine.managers

import engine.Configuration
import shared.Bullet
import shared.Entity
import shared.Player
import shared.Object
import kotlin.math.*

class VisibilityManager : BaseManager<Unit>() {
    fun register(entity: Entity) {
        super.register(entity, Unit)
    }

    fun remove(entity: Entity) {
        super.delete(entity)
    }

    fun check(arr: Array<Pair<Entity, Entity>>?) {
        if (arr != null) {
            for ((e1, e2) in arr) {
                if (e1 is Bullet) {
                    remove(e1)
                }
                if (e2 is Bullet) {
                    remove(e2)
                }
            }
        }
    }

    fun visible(player: Player): Array<Entity> {
        val visibleEntities = mutableListOf<Entity>()
        for (ent2 in entitiesData.keys) {
            if (hypot(player.x - ent2.x, player.y - ent2.y) <
            Configuration.width / 1.8) {
                visibleEntities.add(ent2)
            /*var cnt = 0
            val h = Configuration.sizeOfObj
            if (ent2 is Object) visibleEntities.add(ent2)
            if ((ent2 is Bullet || ent2 is Player) && ent2.id != player.id) {
                visibleEntities.add(player)
                for (ent3 in entitiesData.keys) {
                    if (ent3 is Object) {
                        val a = player.y - ent2.y
                        val b = ent2.x - player.x
                        val c = player.x * ent2.y - ent2.x * player.y
                        val a1 = (a / b).pow(2.0) + 1
                        val b1 = ent3.x - a * c / b - a * ent3.y / b
                        val c1 = ent3.x.pow(2.0) + ent3.y.pow(2.0) + 2 * ent3.y * c / b +
                                (c / b).pow(2.0) - h.pow(2.0)
                        val d = b1.pow(2.0) - a1 * c1
                        if (d > 0) {
                            val x1 = (b1 - sqrt(d)) / a1
                            val y1 = (-c - a * x1) / b
                            if (abs((player.x - x1) / (player.x - ent2.x) -
                                    (player.y - y1) / (player.y - ent2.y)) < 1e-6 &&
                                    (hypot(player.x - x1, player.y - y1) <
                                            hypot(player.x - ent2.x, player.y - ent2.y))) {
                                cnt++
                            }
                        }
                    }
                }
                if (cnt == 0) visibleEntities.add(ent2)
            }
            }
        }
        return visibleEntities.toTypedArray()
    }
}*/