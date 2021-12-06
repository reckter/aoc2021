package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toIntegers
import java.math.BigInteger

class Day6 : Day {
    override val day = 6

    private val start by lazy {
        loadInput()
            .first()
            .split(",")
            .toIntegers()
            .groupBy { it }
            .mapValues { it.value.size.toBigInteger() }
    }

    fun calculatePopulationAtDay(population: Map<Int, BigInteger>, day: Int): Map<Int, BigInteger> {
        return (0 until day)
            .fold(population) { acc, cur ->
                val reduced = acc
                    .mapKeys { it.key - 1 }
                val pregnant = reduced[-1]
                if (pregnant == null)
                    reduced
                else
                    reduced + listOf(
                        (6 to pregnant + (reduced[6] ?: 0.toBigInteger())),
                        (8 to pregnant)
                    ) - listOf(-1)
            }
    }
    override fun solvePart1() {
        calculatePopulationAtDay(start, 80)
            .values
            .sumOf { it }
            .solution(1)
    }

    override fun solvePart2() {
        calculatePopulationAtDay(start, 256)
            .values
            .sumOf { it }
            .solution(2)
    }
}

fun main() = solve<Day6>()
