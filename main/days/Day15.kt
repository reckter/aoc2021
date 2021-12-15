package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.print
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import java.lang.Integer.min
import java.util.PriorityQueue
import kotlin.math.max

class Day15 : Day {
    override val day = 15

    data class Path(
        val points: List<Cord2D<Int>>,
        val score: Int
    )

    fun findShortestPath(map: Map<Cord2D<Int>, Int>, start: Cord2D<Int>, end: Cord2D<Int>): Path {
        val queue = PriorityQueue<Path>(Comparator.comparing {
            it.score
        })

        val seen = mutableSetOf(start)
        queue.add(Path(listOf(start), 0))

        while (queue.isNotEmpty()) {
            val next = queue.remove()

            if (next.points.last() == end) {
                return next
            }
            next.points.last().getNeighbors(true)
                .filter { it in map }
                .filter { it !in seen }
                .forEach {
                    seen.add(it)
                    queue.add(
                        Path(
                            next.points + it,
                            next.score + map[it]!!
                        )
                    )
                }
        }
        error("no path  found!")
    }

    override fun solvePart1() {
        val map =
            loadInput()
                .mapIndexed { y, row ->
                    row.mapIndexed { x, levelString ->
                        Cord2D(x, y) to levelString.toString().toInt()
                    }
                }
                .flatten()
                .toMap()

        val end = run {
            Cord2D(
                map.keys.maxByOrNull { it.x }!!.x,
                map.keys.maxByOrNull { it.y }!!.y
            )
        }

        findShortestPath(map, Cord2D(0, 0), end).score.solution(1)
    }

    fun Map<Cord2D<Int>, Int>.plus(other: Map<Cord2D<Int>, Int>): Map<Cord2D<Int>, Int> {
        return (this.keys + other.keys)
            .associateWith {
                this.getOrDefault(it, 0) + other.getOrDefault(it, 0)
            }
    }

    fun shiftMap(map: Map<Cord2D<Int>, Int>, xFactor: Int, yFactor: Int): Map<Cord2D<Int>, Int> {
        val maxX = map.keys.maxByOrNull { it.x }!!.x
        val maxY = map.keys.maxByOrNull { it.y }!!.y
        val shift = Cord2D((maxX + 1) * xFactor, (maxY + 1) * yFactor)

        return map
            .mapKeys { it.key + shift }
            .mapValues {
                val added = (it.value + xFactor + yFactor)
                if (added > 9) added - 9 else added
            }
    }

    override fun solvePart2() {
        val map =
            loadInput()
                .mapIndexed { y, row ->
                    row.mapIndexed { x, levelString ->
                        Cord2D(x, y) to levelString.toString().toInt()
                    }
                }
                .flatten()
                .toMap()

        val fullMap = (0..4)
            .flatMap { y ->
                (0..4).map { x ->
                    shiftMap(map, x, y)
                }
            }
            .reduce { acc, cur -> acc.plus(cur) }

        val end = run {
            Cord2D(
                fullMap.keys.maxByOrNull { it.x }!!.x,
                fullMap.keys.maxByOrNull { it.y }!!.y
            )
        }

        findShortestPath(fullMap, Cord2D(0, 0), end).score.solution(2)
    }

    fun printMap(map: Map<Cord2D<Int>, Int>): String {
        val minX = map.keys.minByOrNull { it.x }!!.x
        val maxX = map.keys.maxByOrNull { it.x }!!.x
        val minY = map.keys.minByOrNull { it.y }!!.y
        val maxY = map.keys.maxByOrNull { it.y }!!.y

        var ret = "\n"
        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                ret += map[Cord2D(x, y)] ?: " "
            }
            ret += "\n"
        }
        return ret
    }
}

fun main() = solve<Day15>()
