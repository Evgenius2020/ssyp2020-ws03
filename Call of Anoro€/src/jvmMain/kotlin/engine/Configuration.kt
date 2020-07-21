package engine
//TYPES!!!!! -- for Positions Manager
//type 0 = player
//type 1 = bullet
//type 2 = object
object Configuration {
    const val radiusOfPlayer = 16.0
    const val radiusOfBullet = 2.0
    const val sizeOfObj = 16.0
    const val speedOfPlayer = 1.0
    const val speedOfBullet = 5.0
    const val shootCD = 1.5 //seconds
    const val baseRespawnTime = 3.0 //seconds
    const val gameTime = 300.0 //seconds = 5 min
    const val healthOfPlayer = 100
    const val baseDamage = 35
    const val dt = 0.016
}