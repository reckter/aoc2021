package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.print
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAtEmptyLine
import me.reckter.aoc.swapDimensions
import me.reckter.aoc.toIntegers

class Day4 : Day {
    override val day = 4

    fun List<List<Int>>.isFinished(number: List<Int>): Boolean {
        val flags = this.map { it.map { it in number } }

        if (flags.any { it.all { it } }) return true
        if (flags.swapDimensions().any { it.all { it } }) return true
//        if (flags[0][0] && flags[1][1] && flags[2][2] && flags[3][3] && flags[4][4]) return true
//        if (flags[0][4] && flags[1][3] && flags[2][2] && flags[3][1] && flags[4][0]) return true

        return false
    }

    fun List<List<Int>>.getScore(number: List<Int>): Int {
        return this.flatten()
            .filter { it !in number }
            .sum() * number.last()
    }

    private val numbers = loadInput()
        .first()
        .split(",")
        .toIntegers()

    private val boards = loadInput(trim = false)
        .drop(2)
        .splitAtEmptyLine()
        .map {
            it.map {
                it.trim().split(" ").filter { it.isNotBlank() }.toIntegers()
            }
        }

        .print("boards")

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
        generateSequence((1 to null) as Pair<Int, Int?>) { (indice, last) ->
            if (last != null) return@generateSequence null

            val nums = numbers.take(indice)

            val last =
                boards.filter { !it.isFinished(nums) }

            if (last.size != 1) return@generateSequence  (indice + 1) to null

            val board = last.last()
            val finishedAfter = (indice..numbers.size)
                .find {
                    board.isFinished(numbers.take(it))
                }!!

            val finishedNum = numbers.take(finishedAfter)
            (indice + 1) to board.getScore(finishedNum)
        }
            .last()
            .second
            .solution(2)
    }
}

fun main() = solve<Day4>()
