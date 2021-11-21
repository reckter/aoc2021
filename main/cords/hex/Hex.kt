package me.reckter.aoc.hex

data class Hex(
    val x: Int,
    val y: Int,
    val z: Int
) {
    enum class Direction(val direction: Hex) {
        NorthEast(Hex(1, 0, -1)),
        East(Hex(+1, -1, 0)),
        SouthEast(Hex(0, -1, +1)),
        SouthWest(Hex(-1, 0, +1)),
        West(Hex(-1, +1, 0)),
        NorthWest(Hex(0, +1, -1)),
    }
}

operator fun Hex.plus(other: Hex): Hex {
    return Hex(
        this.x + other.x,
        this.y + other.y,
        this.z + other.z
    )
}

fun Hex.getNeighbors(): List<Hex> {
    return Hex.Direction.values().map { this + it.direction }
}
