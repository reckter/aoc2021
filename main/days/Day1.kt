package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toIntegers
import java.util.ArrayDeque

class Day1 : Day {
    override val day = 1

    override fun solvePart1() {
        loadInput()
            .toIntegers()
            .zipWithNext()
            .map { (a, b) -> b - a }
            .count { it > 0 }
            .solution(1)

    }

    override fun solvePart2() {
        loadInput()
            .toIntegers()
            .asSequence()
            .windowed(3, 1)
            .map { it.sum() }
            .zipWithNext()
            .map { (a, b) -> b - a }
            .count { it > 0 }
            .solution(2)
    }
}

fun main() = solve<Day1>()
