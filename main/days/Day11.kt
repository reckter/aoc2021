package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day11 : Day {
    override val day = 11

    fun Map<Cord2D<Int>, Int>.step(): Pair<Map<Cord2D<Int>, Int>, Int> {
        val mutable = this
            .mapValues { it.value + 1 }
            .toMutableMap()

        val flashedCord = mutableSetOf<Cord2D<Int>>()

        var didFlash = true
        while (didFlash) {
            didFlash = false

            mutable
                .filter { it.value > 9 }
                .filter { it.key !in flashedCord }
                .forEach {
                    didFlash = true
                    flashedCord.add(it.key)
                    it.key.getNeighbors()
                        .filter { it in mutable }
                        .forEach { neighbor ->
                            mutable[neighbor] = mutable[neighbor]!! + 1
                        }
                }
        }

        val flashes = mutable.count { it.value > 9 }

        return mutable.mapValues { if (it.value > 9) 0 else it.value } to flashes
    }
    fun printMap(map: Map<Cord2D<Int>, Int>) {
        val minX = map.keys.minByOrNull { it.x }!!.x
        val maxX = map.keys.maxByOrNull { it.x }!!.x
        val minY = map.keys.minByOrNull { it.y }!!.y
        val maxY = map.keys.maxByOrNull { it.y }!!.y

        println("map:")
        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                print(map[Cord2D(x, y)])
            }
            println()
        }
    }

    val map by lazy {
        loadInput()
            .map { it.toCharArray().map { it.toString().toInt() } }
            .mapIndexed { y, row ->
                row.mapIndexed { x, level ->
                    Cord2D(x, y) to level
                }
            }
            .flatten()
            .toMap()
    }

    override fun solvePart1() {

        (0 until 100).fold(map to 0) { (map, count), _ ->
            val (next, flashes) = map.step()


            next to (flashes + count)
        }
            .second
            .solution(1)
    }

    override fun solvePart2() {

        generateSequence(map to 0) { (it, count) ->
            it.step().first to (count + 1)
        }
            .find { (it, _) -> it.count { it.value == 0 } == it.count() }
            ?.second
            ?.solution(2)
    }
}

fun main() = solve<Day11>()
