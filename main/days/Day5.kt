package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.math.sign

class Day5 : Day {
    override val day = 5

    data class Line(
        val from: Cord2D<Int>,
        val to: Cord2D<Int>
    )

    fun Line.getPoints(): List<Cord2D<Int>> {
        val xDiff = to.x - from.x
        val yDiff = to.y - from.y

        val xFactor = sign(xDiff.toDouble()).toInt()
        val yFactor = sign(yDiff.toDouble()).toInt()
        val max = max(abs(xDiff), abs(yDiff))

        return (0..(max))
            .map { from + Cord2D(it * xFactor, it * yFactor) }
    }

    val lines by lazy {
        loadInput()
            .parseWithRegex("(\\d+),(\\d+) -> (\\d+),(\\d+)")
            .map { (x1, y1, x2, y2) ->
                Line(Cord2D(x1.toInt(), y1.toInt()), Cord2D(x2.toInt(), y2.toInt()))
            }
    }

    override fun solvePart1() {
        lines
            .filter { it.from.x == it.to.x || it.from.y == it.to.y }
            .flatMap { it.getPoints() }
            .groupBy { it }
            .values
            .count { it.size > 1 }
            .solution(1)
    }

    override fun solvePart2() {
        lines
            .flatMap { it.getPoints() }
            .groupBy { it }
            .values
            .count { it.size > 1 }
            .solution(2)
    }
}

fun main() = solve<Day5>()
