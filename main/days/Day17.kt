package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import java.lang.Integer.max
import kotlin.math.sign

class Day17 : Day {
    override val day = 17

    data class Probe(
        val cord: Cord2D<Int>,
        val speed: Cord2D<Int>,
    ) {
        fun tick(): Probe {
            return Probe(
                cord + speed,
                Cord2D(speed.x - speed.x.sign, speed.y - 1)
            )
        }
    }

    override fun solvePart1() {
        val (minX, maxX, minY, maxY) = loadInput()
            .parseWithRegex("target area: x=(-?\\d*)..(-?\\d*), y=(-?\\d*)..(-?\\d*)")
            .first()
            .let { (minXStr, maxXStr, minYStr, maxYStr) ->
                listOf(minXStr.toInt(), maxXStr.toInt(), minYStr.toInt(), maxYStr.toInt())
            }

        val start = Cord2D(0, 0)

        (0..1000)
            .flatMap { x -> (0..1000).map { y -> Cord2D(x, y) } }
            .map { Probe(start, it) }
            .filter {
                var probe = it
                var found: Boolean? = null
                while (found == null) {
                    probe = probe.tick()
                    if (probe.cord.x in minX..maxX &&
                        probe.cord.y in minY..maxY
                    ) {
                        found = true
                    }

                    if (probe.cord.x > maxX || probe.cord.y < minY) {
                        found = false
                    }
                }
                found
            }
            .maxOf {
                var probe = it
                var max = probe.cord.y
                while (max <= probe.cord.y) {
                    probe = probe.tick()
                    max = max(max, probe.cord.y)
                }
                max
            }
            .solution(1)
    }

    override fun solvePart2() {
        val (minX, maxX, minY, maxY) = loadInput()
            .parseWithRegex("target area: x=(-?\\d*)..(-?\\d*), y=(-?\\d*)..(-?\\d*)")
            .first()
            .let { (minXStr, maxXStr, minYStr, maxYStr) ->
                listOf(minXStr.toInt(), maxXStr.toInt(), minYStr.toInt(), maxYStr.toInt())
            }

        val start = Cord2D(0, 0)

        (1..maxX)
            .flatMap { x -> (minY..((maxX / x) * 15)).map { y -> Cord2D(x, y) } }
            .map { Probe(start, it) }
            .filter {
                var probe = it
                var found: Boolean? = null
                while (found == null) {
                    probe = probe.tick()
                    if (probe.cord.x in minX..maxX &&
                        probe.cord.y in minY..maxY
                    ) {
                        found = true
                    }

                    if (probe.cord.x > maxX || probe.cord.y < minY) {
                        found = false
                    }
                }
                found
            }
            .map { it.cord }
            .count()
            .solution(2)
    }
}

fun main() = solve<Day17>()
