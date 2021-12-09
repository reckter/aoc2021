package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.rotateLeft
import me.reckter.aoc.solution
import me.reckter.aoc.time
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
            .fold(mutableListOf(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L)) { acc, cur ->
                acc[cur.key] = cur.value

                acc
            }
    }

    private fun calculatePopulationAtDay(population: List<Long>, day: Int): List<Long> {
        var acc = population.toTypedArray()

        var i = 0
        while (i < day / 18) {
            i++

            // This does 18 days at a time
            val zero = acc[0]
            val one = acc[1]
            val two = acc[2]
            val three = acc[3]

            acc[0] = acc[0] + acc[2] * 2 + acc[4]
            acc[1] = acc[1] + acc[3] * 2 + acc[5]
            acc[2] = acc[2] + acc[4] * 2 + acc[6]
            acc[3] = acc[3] + acc[5] * 2 + acc[7] + zero
            acc[4] = acc[4] + acc[6] * 2 + acc[8] + one
            acc[5] = acc[5] + acc[7] * 2 + zero * 3 + two
            acc[6] = acc[6] + acc[8] * 2 + one * 3 + three
            acc[7] = acc[7] + zero * 2 + two
            acc[8] = acc[8] + one * 2 + three
        }

        i = 0
        while (i < day % 18) {
            i++
            val tmp = acc[0]
            acc[0] = acc[1]
            acc[1] = acc[2]
            acc[2] = acc[3]
            acc[3] = acc[4]
            acc[4] = acc[5]
            acc[5] = acc[6]
            acc[6] = acc[7] + tmp
            acc[7] = acc[8]
            acc[8] = tmp
        }
        return acc.toList()
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

fun main() = time<Day6>()
