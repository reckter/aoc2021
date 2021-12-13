package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import kotlin.math.abs

class Day13 : Day {
    override val day = 13

    val initialPoints by lazy {
        loadInput()
            .takeWhile { it.isNotBlank() }
            .parseWithRegex("(\\d*),(\\d*)")
            .map { (x,y) -> Cord2D(x.toInt(),y.toInt()) }
    }

    val instructions by lazy {
        loadInput()
            .parseWithRegex("fold along (.)=(\\d*)")
            .map { (direction, number) -> direction to number.toInt() }
    }


    fun List<Cord2D<Int>>.oneFold(instruction: Pair<String, Int>): List<Cord2D<Int>> {
        return this.map {
            when (instruction.first) {
                "x" -> it.copy(x = instruction.second - abs(instruction.second - it.x))
                "y" -> it.copy(y = instruction.second - abs(instruction.second - it.y))
                else -> error("wrong instruction!")
            }
        }
            .distinct()
    }

    override fun solvePart1() {
        val map = initialPoints.oneFold(instructions.first())
            .count()
            .solution(1)

    }

    fun printMap(map: List<Cord2D<Int>>):String {
        val minX = map.minByOrNull { it.x }!!.x
        val maxX = map.maxByOrNull { it.x }!!.x
        val minY = map.minByOrNull { it.y }!!.y
        val maxY = map.maxByOrNull { it.y }!!.y

        val inMap = map.toSet()

        var ret = "\n"
        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                ret += if(Cord2D(x,y) in inMap) "X" else " "
            }
            ret += "\n"
        }
        return ret
    }
    override fun solvePart2() {
        val code = instructions.fold(initialPoints) { map, fold ->
            map.oneFold(fold)
        }

        printMap(code)
            .solution(2)



    }


}

fun main() = solve<Day13>()
