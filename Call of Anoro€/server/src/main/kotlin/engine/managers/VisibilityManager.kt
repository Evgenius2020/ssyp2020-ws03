package engine.managers

import engine.Configuration
import shared.Bullet
import shared.Entity
import shared.Player
import shared.Object
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sqrt

class VisibilityManager : BaseManager<Unit>() {
    fun register(entity: Entity) {
        super.register(entity, Unit)
    }

    fun remove(entity: Entity) {
        super.delete(entity)
    }

    fun visible(player: Player): Array<Entity> {
        val visibleEntities = mutableListOf<Entity>()
        val h = Configuration.sizeOfObj
        visibleEntities.add(player)
        for (ent2 in entitiesData.keys) {
            var cnt = 0
            if (ent2 is Object) visibleEntities.add(ent2)
            //if (hypot(player.x - ent2.x, player.y - ent2.y) <
                    //Configuration.width / 1.8) {
                if ((ent2 is Bullet || ent2 is Player) && ent2.id != player.id) {
                    for (ent3 in entitiesData.keys) {
                        if (ent3 is Object) {
                            val a = player.y - ent2.y
                            val b = ent2.x - player.x
                            val c = player.x * ent2.y - ent2.x * player.y
                            if ((hypot(player.x - ent2.x, player.y - ent2.y) >
                                            hypot(player.x - ent3.x, player.y - ent3.y)) &&
                                    (((a / b > 0) && ((h * a - ent3.x * a - c) / b > ent3.y - h) &&
                                            (ent3.y + h > (-h * a - ent3.x * a - c) / b)) ||
                                            ((a / b < 0) && ((-h * a - ent3.x * a - c) / b > ent3.y - h) &&
                                                    (ent3.y + h > (h * a - ent3.x * a - c) / b)))) {
                                cnt++
                            }
                        }
                    }
                    if (cnt == 0) visibleEntities.add(ent2)
                }
            //}
        }
        return visibleEntities.toTypedArray()
    }
}