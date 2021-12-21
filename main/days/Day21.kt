package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.print
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day21 : Day {
    override val day = 21

    fun Pair<Int, Int>.turn(diceSequence: Sequence<Int>): Pair<Int, Int> {
        return (first + diceSequence.take(3).sum()) % 10 to (second + diceSequence.drop(3).take(3).sum()) % 10
    }

    fun findFirstWinner(player1Start: Int, player2Start: Int, diceSequence: Sequence<Int>): Int {
        (0..1000)
            .fold(player1Start to player2Start to (0 to 0)) {(cur, points), turn ->
                val next = cur.turn(diceSequence.drop(turn * 6))
                val nextPoints = (points.first + next.first + 1) to ( points.second + next.second + 1)
                if (nextPoints.first >= 1000) {
                    return (turn * 6 + 3)* points.second
                }
                if (nextPoints.second >= 1000) {
                    return (turn +1) * 6 * nextPoints.first
                }
                next to nextPoints
            }
        return -1
    }

    val cache = mutableMapOf<Pair<Pair<Int, Int>, Pair<Int, Int>>, Pair<Long, Long>>()

    override fun solvePart1() {
        val diceSequence = generateSequence(1) {
            if(it == 100) 1 else it + 1
        }

        val (player1Start, player2Start) = loadInput()
            .parseWithRegex("Player (\\d) starting position: (\\d)")
            .map { (player, startingPosition) -> startingPosition.toInt() }
            .map { it - 1}

        findFirstWinner(player1Start, player2Start, diceSequence)
            .solution(1)

    }

    val timesMap = mapOf(
        3 to 1,
        4 to 3,
        5 to 6,
        6 to 7,
        7 to 6,
        8 to 3,
        9 to 1,
    )

    fun turn2(pos: Pair<Pair<Int, Int>, Pair<Int,Int>>): Pair<Long,Long> {
        if (pos.second.first >= 21) {
            return 1L to 0L
        }
        if (pos.second.second >= 21) {
            return 0L to 1L
        }

        if (pos in cache) {
            return cache[pos]!!
        }

        val result = (3..9)
            .map {
                val field = (pos.first.first + it) % 10
                val score = (pos.second.first + field + 1)

                val one = turn2((pos.first.second to field) to (pos.second.second to score))
                val times = timesMap[it]!!
                (one.second * times) to (one.first * times)
            }
            .reduce { acc, cur -> (acc.first + cur.first) to (acc.second + cur.second)}

        cache[pos] = result
        return result
    }

    override fun solvePart2() {
        val (player1Start, player2Start) = loadInput()
            .parseWithRegex("Player (\\d) starting position: (\\d)")
            .map { (player, startingPosition) -> startingPosition.toInt() }
            .map { it - 1}


        turn2((player1Start to player2Start) to (0 to 0))
            .print("sol")
            .let { listOf(it.first, it.second) }
            .maxOrNull()
            .solution(2)

    }
}

fun main() = solve<Day21>()
