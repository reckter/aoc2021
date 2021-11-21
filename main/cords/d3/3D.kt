package me.reckter.aoc.cords.d3

/**
 *           3D
 */

data class Cord3D<T : Number>(
    val x: T,
    val y: T,
    val z: T
)

operator fun Cord3D<Int>.plus(other: Cord3D<Int>): Cord3D<Int> {
    return Cord3D(
        this.x + other.x,
        this.y + other.y,
        this.z + other.z
    )
}

fun Cord3D<Int>.getNeighbors(): List<Cord3D<Int>> {
    return (-1..1).flatMap { xOffset ->
        (-1..1).flatMap { yOffset ->
            (-1..1).map { zOffset ->
                this + Cord3D(xOffset, yOffset, zOffset)
            }
        }
    }
        .filter { it != this }
}
