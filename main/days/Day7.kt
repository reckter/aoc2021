package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toIntegers
import java.lang.Math.abs

class Day7 : Day {
    override val day = 7

    override fun solvePart1() {
        loadInput()
            .first()
            .split(",")
            .toIntegers()
            .let { list ->
                (list.minOf { it }..list.maxOf { it })
                    .minOfOrNull { point ->
                        list.sumOf { abs(it - point) }
                    }
            }
            .solution(1)
    }

    override fun solvePart2() {
        loadInput()
            .first()
            .split(",")
            .toIntegers()
            .let { list ->
                (list.minOf { it }..list.maxOf { it })
                    .minOfOrNull { point ->
                        list.map { abs(it - point) }
                            .map { (it + 1) * (it.toDouble() / 2) }
                            .sumOf { it.toLong() }
                    }
            }
            .solution(2)
    }
}

fun main() = solve<Day7>()
