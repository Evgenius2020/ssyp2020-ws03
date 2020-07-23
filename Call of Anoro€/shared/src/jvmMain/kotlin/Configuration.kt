package engine

object Configuration {
    const val fps = 60
    const val radiusOfPlayer = 16.0
    const val radiusOfBullet = 2.0
    const val sizeOfObj = 16.0
    const val speedOfPlayer = 1.0
    const val speedOfBullet = 5.0
    const val shootCD = (1.5 * fps).toInt() //seconds * fps => frames
    const val baseRespawnTime = (0.2 * fps).toInt() //seconds * fps => frames
    const val gameTime = 300 * fps //seconds * fps => frames
    const val healthOfPlayer = 100
    const val baseDamage = 33
    const val width = 640.0
    const val height = 640.0
    const val boomDuration = 1 * fps // seconds * fps => frames
}