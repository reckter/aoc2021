package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import kotlin.math.pow

class Day20 : Day {
    override val day = 20

    private val rule by lazy {
        loadInput()
            .first()
            .mapIndexed { index, c -> index to (c == '#') }
            .toMap()
    }

    fun List<Boolean>.toInt(): Int {
        return this.reversed()
            .mapIndexed { index, c -> 2.0.pow(index) * if (c) 1 else 0 }
            .sum()
            .toInt()
    }

    fun Map<Cord2D<Int>, Boolean>.enhance(infinityIsCurrently: Boolean): Pair<Map<Cord2D<Int>, Boolean>, Boolean> {
        val minX = this.keys.minOf { it.x } - 1
        val maxX = this.keys.maxOf { it.x } + 1
        val minY = this.keys.minOf { it.y } - 1
        val maxY = this.keys.maxOf { it.y } + 1

        return (minX..maxX)
            .flatMap { x ->
                (minY..maxY)
                    .map { y -> Cord2D(x, y) }
            }
            .map {
                val bits = (-1..1).flatMap { yOffset ->
                    (-1..1).map { xOffset ->
                        it + Cord2D(xOffset, yOffset)
                    }
                }
                    .map { this[it] ?: infinityIsCurrently }

                it to (rule[bits.toInt()] ?: false)
            }
            .toMap() to rule[if (infinityIsCurrently) 511 else 0]!!
    }

    fun Pair<Map<Cord2D<Int>, Boolean>, Boolean>.enhance(): Pair<Map<Cord2D<Int>, Boolean>, Boolean> =
        this.first.enhance(this.second)

    fun printMap(map: Map<Cord2D<Int>, Boolean>): String {
        val minX = map.keys.minByOrNull { it.x }!!.x
        val maxX = map.keys.maxByOrNull { it.x }!!.x
        val minY = map.keys.minByOrNull { it.y }!!.y
        val maxY = map.keys.maxByOrNull { it.y }!!.y

        var ret = "\n"
        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                ret += if (map[Cord2D(x, y)] == true) "#" else " "
            }
            ret += "\n"
        }
        return ret
    }

    override fun solvePart1() {
        val start = loadInput()
            .drop(1)
            .mapIndexed { y, row ->
                row.mapIndexed { x, c -> Cord2D(x, y) to (c == '#') }
            }
            .flatten()
            .toMap()


        (start to false)
            .enhance()
            .enhance()
            .first
            .count { it.value }
            .solution(1)
    }

    override fun solvePart2() {
        val start = loadInput()
            .drop(1)
            .mapIndexed { y, row ->
                row.mapIndexed { x, c -> Cord2D(x, y) to (c == '#') }
            }
            .flatten()
            .toMap()


        (0 until 50).fold(start to false)  {acc, _ ->
            acc.enhance()
        }
            .first
            .count { it.value }
            .solution(2)
    }
}

fun main() = solve<Day20>()
