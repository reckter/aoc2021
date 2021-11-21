package me.reckter.aoc.cords.d2

import me.reckter.aoc.cords.d3.plus

/**
 *           2D
 */

data class Cord2D<T : Number>(
    val x: T,
    val y: T
)

operator fun Cord2D<Int>.plus(other: Cord2D<Int>): Cord2D<Int> {
    return Cord2D(
        this.x + other.x,
        this.y + other.y
    )
}

fun Cord2D<Int>.getNeighbors(noEdges: Boolean = false): List<Cord2D<Int>> {
    if (noEdges)
        return listOf(
            0 to -1,
            0 to 1,
            -1 to 0,
            1 to 0
        )
            .map {
                this + Cord2D(it.first, it.second)
            }

    return (-1..1).flatMap { xOffset ->
        (-1..1).map { yOffset ->
            this + Cord2D(xOffset, yOffset)
        }
    }
        .filter { it != this }
}
