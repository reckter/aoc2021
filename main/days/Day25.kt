package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day25 : Day {
    override val day = 25

    data class Bounds(
        val x: IntRange,
        val y: IntRange,
    )

    fun Bounds.wrap(cord: Cord2D<Int>): Cord2D<Int> {
        if (cord.x in this.x && cord.y in this.y) return cord

        if (cord.x > this.x.last) return Cord2D(0, cord.y)

        return Cord2D(cord.x, 0)
    }

    fun Map<Cord2D<Int>, Char>.tick(bounds: Bounds): Map<Cord2D<Int>, Char> {
        val onlyLeft = this
            .filter { it.value == '>' }
            .mapKeys {
                val nextCord = bounds.wrap(it.key + Cord2D(1, 0))
                if (this[nextCord] == null) {
                    // empty
                    nextCord
                } else
                    it.key
            }

        val afterLeft = this.filterValues { it != '>' } + onlyLeft

        return afterLeft.filterValues{ it != 'v' } + afterLeft
            .filter { it.value == 'v' }
            .mapKeys {
                val nextCord = bounds.wrap(it.key + Cord2D(0, 1))
                if (afterLeft[nextCord] == null) {
                    // empty
                    nextCord
                } else
                    it.key
            }
    }

    fun printMap(bounds: Bounds, map: Map<Cord2D<Int>, Char>) {

        println("map:")
        bounds.x.forEach { x ->
            bounds.y.forEach { y ->
                print(map[Cord2D(x, y)] ?: ' ')
            }
            println()
        }
    }

    override fun solvePart1() {
        val initialMap = loadInput()
            .mapIndexed { y, row ->
                row.mapIndexed { x, col -> Cord2D(x, y) to col }
            }
            .flatten()
            .toMap()

        val bounds = Bounds(
            initialMap.minOf { it.key.x }..initialMap.maxOf { it.key.x },
            initialMap.minOf { it.key.y }..initialMap.maxOf { it.key.y },
        )


        generateSequence(initialMap.filterValues { it != '.' }) { it ->
            val next = it.tick(bounds)

            if (next != it) next else null
        }
            .count()
            .solution(1)
    }

    override fun solvePart2() {
    }
}

fun main() = solve<Day25>()
