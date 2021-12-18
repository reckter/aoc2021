package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.allPairings
import me.reckter.aoc.print
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day18 : Day {
    override val day = 18

    private sealed class SnailNumber {
        abstract fun explode(level: Int = 4): Pair<SnailNumber, Pair<Int?, Int?>?>
        abstract fun reduce(): Pair<SnailNumber, Boolean>
        abstract fun addToRightMost(value: Int): SnailNumber
        abstract fun addToLeftMost(value: Int): SnailNumber
        abstract fun magnitude(): Int

        data class Leaf(val value: Int) : SnailNumber() {
            override fun explode(level: Int) = this to null
            override fun reduce(): Pair<SnailNumber, Boolean> {

                if (this.value >= 10) {
                    val leftValue = value / 2
                    return Tree(Leaf(leftValue), Leaf(value - leftValue)) to true
                }

                return this to false
            }

            override fun addToRightMost(value: Int) = Leaf(this.value + value)

            override fun addToLeftMost(value: Int) = Leaf(this.value + value)
            override fun magnitude(): Int = value

            override fun toString() = value.toString()
        }

        data class Tree(
            val left: SnailNumber,
            val right: SnailNumber
        ) : SnailNumber() {
            override fun explode(level: Int): Pair<SnailNumber, Pair<Int?, Int?>?> {
                if (level <= 1) {
                    // need to explode a child
                    if (left is Tree && left.hasOnlyDirectLeaves()) {
                        // explode left
                        val leftValue = (left.left as? Leaf)?.value ?: error("No direct value!")
                        val rightValue = (left.right as? Leaf)?.value ?: error("No direct value!")
                        val right = right.addToLeftMost(rightValue)

                        return Tree(Leaf(0), right) to (leftValue to null)
                    }

                    if (right is Tree && right.hasOnlyDirectLeaves()) {
                        // explode right
                        val leftValue = (right.left as? Leaf)?.value ?: error("No direct value!")
                        val rightValue = (right.right as? Leaf)?.value ?: error("No direct value!")
                        val left = left.addToRightMost(leftValue)

                        return Tree(left, Leaf(0)) to (null to rightValue)
                    }
                }
                val (leftTmp, leftTodo) = left.explode(level - 1)
                if (leftTodo != null) {
                    if (leftTodo.second != null) {
                        return Tree(
                            leftTmp,
                            right.addToLeftMost(leftTodo.second!!)
                        ) to (null to null)
                    }

                    return Tree(leftTmp, right) to leftTodo
                }

                val (rightTmp, rightTodo) = right.explode(level - 1)
                if (rightTodo != null) {
                    if (rightTodo.first != null) {
                        return Tree(
                            left.addToRightMost(rightTodo.first!!),
                            rightTmp
                        ) to (null to null)
                    }

                    return Tree(left, rightTmp) to rightTodo
                }

                return this to null
            }

            override fun reduce(): Pair<SnailNumber, Boolean> {
                val (leftValue, didLeftReduce) = left.reduce()
                if (didLeftReduce) {
                    return Tree(leftValue, right) to true
                }
                val (rightValue, didRightReduce) = right.reduce()

                return Tree(left, rightValue) to didRightReduce
            }

            fun hasOnlyDirectLeaves() = left is Leaf && right is Leaf

            override fun toString() = "[$left,$right]"

            override fun addToRightMost(value: Int): SnailNumber =
                Tree(left, this.right.addToRightMost(value))

            override fun addToLeftMost(value: Int): SnailNumber =
                Tree(this.left.addToLeftMost(value), right)

            override fun magnitude(): Int = left.magnitude() * 3 + right.magnitude() * 2
        }
    }

    private fun parseNumber(input: String): Pair<SnailNumber, String> {
        if (input.startsWith("[")) {
            val (left, leftRest) = parseNumber(input.drop(1))
            assert(leftRest.first() == ',') { "expected ',' got $leftRest" }
            val (right, rightRest) = parseNumber(leftRest.drop(1))
            assert(rightRest.first() == ']') { "expected ',' got $leftRest" }
            return SnailNumber.Tree(left, right) to rightRest.drop(1)
        }

        val value = input.split("[,\\]]".toRegex()).first().toInt()
        return SnailNumber.Leaf(value) to input.removePrefix(value.toString())
    }

    private fun SnailNumber.reduced(): SnailNumber {
        var ret = this

        while (true) {
            val (exploded) = ret.explode()
            if (ret != exploded) {
                ret = exploded
                continue
            }
            val (reduced) = ret.reduce()
            if (ret == reduced) {
                break
            }
            ret = reduced
        }
        return ret
    }

    override fun solvePart1() {
        loadInput()
            .map { parseNumber(it).first }
            .reduce {acc, cur -> SnailNumber.Tree(acc, cur).reduced()}
            .magnitude()
            .solution(1)
    }

    override fun solvePart2() {
        loadInput()
            .map { parseNumber(it).first }
            .allPairings()
            .map { (a,b) -> SnailNumber.Tree(a, b).reduced().magnitude()}
            .maxOrNull()
            .solution(2)
    }
}

fun main() = solve<Day18>()
