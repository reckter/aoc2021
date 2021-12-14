package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.permutations
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day8 : Day {
    override val day = 8

    val digits = mapOf<Int, List<String>>(
        0 to listOf("a", "b", "c", "e", "f", "g"),
        1 to listOf("c", "f"),
        2 to listOf("a", "c", "d", "e", "g"),
        3 to listOf("a", "c", "d", "f", "g"),
        4 to listOf("b", "c", "d", "f"),
        5 to listOf("a", "b", "d", "f", "g"),
        6 to listOf("a", "b", "d", "e", "f", "g"),
        7 to listOf("a", "c", "f"),
        8 to listOf("a", "b", "c", "d", "e", "f", "g"),
        9 to listOf("a", "b", "c", "d", "f", "g"),
    )

    override fun solvePart1() {
        loadInput()
            .map { it.split("|").last() }
            .flatMap { it.split(" ") }
            .filter { it.isNotEmpty() }
            .count { it.length in listOf(2, 3, 4, 7) }
            .solution(1)
    }

    override fun solvePart2() {
        val allPerms = digits[8]!!.permutations()
            .map { it.zip(digits[8]!!).associateBy { it.second }.mapValues { it.value.first } }
        loadInput()
            .map {
                val (wiringStr, outputStr) = it.split("|")
                val wiring = wiringStr.split(" ").filter { it.isNotBlank() }
                val output = outputStr.split(" ").filter { it.isNotBlank() }

                val validPerms = allPerms
                    .filter { perm ->
                        wiring
                            .all {
                                val onSegments = it.split("").filter { it.isNotBlank() }
                                    .map { perm[it]!! }

                                digits
                                    .values
                                    .any { it.size == onSegments.size && it.all { it in onSegments } }
                            }
                    }
                val validPerm = validPerms.single()

//                validPerm.print("perm")

                output
                    .map {
                        val onSegments = it.split("").filter { it.isNotBlank() }
                            .map { validPerm[it]!! }

                        digits
                            .entries
                            .find { (key, it) -> it.size == onSegments.size && it.all { it in onSegments } }
                            ?.let { it.key } ?: error("no digit found!")
                    }
                    .reduce { acc, it ->
                        acc * 10 + it
                    }
            }
            .sum()
            .solution(2)

//        loadInput(2)
//            .map {
//                val (wiringStr, outputStr) = it.split("|")
//                val wiring = wiringStr.split(" ")
//                val output = outputStr.split(" ")
//
//                var guess = wiring
//                    .filter { it.isNotEmpty() }
//                    .sortedBy { it.length }
//                    .fold(emptyMap<String, List<String>>()) { acc, cur ->
//                        val possible = digits.values.filter { it.size == cur.length }
//                            .filter { dig ->
//                                cur.split("").filter { it.isNotBlank() }.all {
//                                    acc[it]?.any { it in dig } ?: true
//                                }
//                            }
//                            .flatten()
//                            .distinct()
//
//                        val guess = acc + cur.split("")
//                            .filter { it.isNotEmpty() }
//                            .associateWith { digit ->
//                                if (digit in acc)
//                                    possible
//                                        .filter {
//                                            it in (acc[digit] ?: digits[8] ?: error("no list"))
//                                        }
//                                else
//                                    possible
//                            }
//
//                        (1..4).fold(guess) { guess, size ->
//                            val foundPairs = guess.filter { it.value.size == size }
//                                .entries
//                                .groupBy { it.value }
//                                .filter { it.value.size == size }
//                                .flatMap { list -> list.value.map { it.key to it.value } }
//                                .toMap()
//
//
//                            guess
//                                .mapValues {
//                                    if (it.key in foundPairs) it.value
//                                    else it.value.filter { it !in foundPairs.values.flatten() }
//                                }
//                        }
//                            .print("guess")
//                    }
//                guess.print("guess")
//            }
    }
}

fun main() = solve<Day8>()
