package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.categorizeWithRegex
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.cords.d3.Cord3D
import me.reckter.aoc.cords.d3.plus
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day2 : Day {
    override val day = 2

    override fun solvePart1() {
        loadInput()
            .categorizeWithRegex("up (\\d+)", "down (\\d+)", "forward (\\d+)")
            .let { (ups, downs, forwards) ->
                ups.map { (numberStr) -> Cord2D(-numberStr.toInt(), 0) } +
                    downs.map { (numberStr) -> Cord2D(numberStr.toInt(), 0) } +
                    forwards.map { (numberStr) -> Cord2D(0, numberStr.toInt()) }
            }
            .reduce { acc, cur -> acc + cur }
            .let { it.x * it.y }
            .solution(1)
    }

    override fun solvePart2() {
        loadInput()
            .fold(Cord3D(0, 0, 0)) { acc, cur ->
                val (direction, numberStr) = cur.split(" ")
                val number = numberStr.toInt()
                when (direction) {
                    "up" -> acc + Cord3D(0, 0, -number)
                    "down" -> acc + Cord3D(0, 0, number)
                    "forward" -> {
                        acc + Cord3D(acc.z * number, number, 0)
                    }
                    else -> error("no valid direction!")
                }
            }
            .let { it.x * it.y }
            .solution(2)
    }
}

fun main() = solve<Day2>()
