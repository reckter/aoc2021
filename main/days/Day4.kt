package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAtEmptyLine
import me.reckter.aoc.swapDimensions
import me.reckter.aoc.toIntegers

class Day4 : Day {
    override val day = 4

    private fun List<List<Int>>.isFinished(number: List<Int>): Boolean {
        val flags = this.map { it.map { it in number } }

        if (flags.any { it.all { it } }) return true
        if (flags.swapDimensions().any { it.all { it } }) return true

        return false
    }

    private fun List<List<Int>>.getScore(number: List<Int>): Int {
        return this.flatten()
            .filter { it !in number }
            .sum() * number.last()
    }

    private val numbers by lazy {
        loadInput()
            .first()
            .split(",")
            .toIntegers()
    }

    private val boards by lazy {
        loadInput(trim = false)
            .drop(2)
            .splitAtEmptyLine()
            .map {
                it.map {
                    it.trim().split(" ").filter { it.isNotBlank() }.toIntegers()
                }
            }
    }

    override fun solvePart1() {
        generateSequence((1 to null) as Pair<Int, Int?>) { (indice, last) ->
            if (last != null) return@generateSequence null

            val nums = numbers.take(indice)

            val finisher =
                boards.find { it.isFinished(nums) } ?: return@generateSequence (indice + 1) to null

            (indice + 1) to finisher.getScore(nums)
        }
            .last()
            .second
            .solution(1)
    }

    override fun solvePart2() {
        boards
            .map { board ->
                val finishedAfter = (1..numbers.size)
                    .find {
                        board.isFinished(numbers.take(it))
                    }!!

                val finishedNum = numbers.take(finishedAfter)
                finishedNum.size to board.getScore(finishedNum)
            }
            .maxByOrNull { it.first }
            ?.second
            .solution(2)
    }
}

fun main() = solve<Day4>()
