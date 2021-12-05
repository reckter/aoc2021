package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.swapDimensions

class Day3 : Day {
    override val day = 3

    override fun solvePart1() {
        loadInput()
            .map { it.split("") }
            .map { it.filter { it != "" } }
            .swapDimensions()
            .map { it.count { it == "1" } > it.count() / 2 }
            .let {
                val gamma = it
                    .map { if (it) "1" else "0" }
                    .joinToString("")
                    .toBigInteger(2)

                val epsilon = it
                    .map { if (it) "0" else "1" }
                    .joinToString("")
                    .toBigInteger(2)

                epsilon * gamma
            }
            .solution(1)
    }

    override fun solvePart2() {
        loadInput()
            .let { list ->
                val oxygen = list.first().indices.fold(list) { cur, index ->
                    val groups = cur.groupBy { it[index] }
                    if (groups.size == 1) return@fold groups.values.single()
                    if (groups['1']!!.size >= groups['0']!!.size) groups['1']!! else groups['0']!!
                }
                    .single()
                    .toInt(2)
                val co2 = list.first().indices.fold(list) { cur, index ->
                    val groups = cur.groupBy { it[index] }
                    if (groups.size == 1) return@fold groups.values.single()
                    if (groups['1']!!.size < groups['0']!!.size) groups['1']!! else groups['0']!!
                }
                    .single()
                    .toInt(2)
                co2 * oxygen
            }
            .solution(2)
    }
}

fun main() = solve<Day3>()
