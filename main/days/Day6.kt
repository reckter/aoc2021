package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.rotateLeft
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toIntegers

class Day6 : Day {
    override val day = 6

    private val start by lazy {
        loadInput()
            .first()
            .split(",")
            .toIntegers()
            .groupBy { it }
            .mapValues { it.value.size.toLong() }
            .entries
            .fold(mutableListOf(0L,0L,0L,0L,0L,0L,0L,0L, 0L)) { acc, cur ->
                acc[cur.key] = cur.value

                acc
            }
    }

    private fun calculatePopulationAtDay(population: List<Long>, day: Int): List<Long> {
        return (0 until day)
            .fold(population.toMutableList()) { acc, cur ->
                val next = acc.rotateLeft(1).toMutableList()

                next[6] += next[8]

                next
            }
    }
    override fun solvePart1() {
        calculatePopulationAtDay(start, 80)
            .sum()
            .solution(1)
    }

    override fun solvePart2() {
        calculatePopulationAtDay(start, 256)
            .sum()
            .solution(2)
    }
}

fun main() = solve<Day6>()
