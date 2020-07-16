package Engine

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


data class Dot(var x: Double, var y: Double){
    fun distanceTo(target: Dot): Double = sqrt(abs(this.x - target.x).pow(2) + abs(this.y - target.y).pow(2))

    fun directionTo(target: Dot): Vector = Vector(target.x - this.x, target.y - this.y).makeLenOne()

    fun add(vector: Vector){
        this.x += vector.x
        this.y += vector.y
    }

}

data class Vector(var x: Double, var y: Double){
    constructor(d: Dot): this(d.x, d.y)

    constructor(src: Dot, dst: Dot): this(dst.x - src.x, dst.y - src.y)

    val len: Double get() = sqrt(this.x.pow(2) + this.y.pow(2))

    fun makeLenOne(): Vector{
        val tmp = len
        x /= tmp
        y /= tmp
        return this
    }

    fun mul(n: Double): Vector {
        x *= n
        y *= n
        return this
    }

    fun makeLen(len: Double): Vector{
        this.makeLenOne()
        this.mul(len)
        return this
    }
}