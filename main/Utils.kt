package me.reckter.aoc

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.nio.file.Files
import java.util.LinkedList
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.reflect.full.createInstance
import kotlin.system.measureNanoTime

object Context {
    var day: Int = 0
    var overwritingSolutions: Boolean = false
    var testMode: Boolean = false

    override fun toString(): String {
        return "Day: $day\nTestMode: $testMode\nOverwriteSolutions: $overwritingSolutions"
    }
}

fun saveSolution(solution: Int, value: String) {
    File("solutions/${Context.day}_$solution.txt").writeText(value)
}

fun checkSolution(solution: Int, value: String) {
    if (!Files.exists(File("solutions/${Context.day}_$solution.txt").toPath())) {
        if (Context.testMode) {
            saveSolution(solution, value)
        }
    }

    val savedValue = File("solutions/${Context.day}_$solution.txt").readText()
    assert(
        value == savedValue
    ) { "day ${Context.day} failed for solution $solution! \nwas: \n$savedValue\n\nnow:\n$value" }
}

fun Long.logTime(solution: String) {
    val timeString = when {
        this > 1_000_000_000 -> "${(this.toDouble() / 1_000_000_000.0).scale(3)}s"
        this > 1_000_000 -> "${(this.toDouble() / 1_000_000.0).scale(3)}ms"
        this > 1_000 -> "${(this.toDouble() / 1_000.0).scale(3)}μs"
        else -> "${this}ns"
    }
    println("$solution took: $timeString")
}

fun Double.logTime(solution: String) {
    val timeString = when {
        this > 1_000_000_000 -> "${(this / 1_000_000_000.0).scale(3)}s"
        this > 1_000_000 -> "${(this / 1_000_000.0).scale(3)}ms"
        this > 1_000 -> "${(this / 1_000.0).scale(3)}μs"
        else -> "${this}ns"
    }
    println("$solution took: $timeString")
}

private fun Double.scale(scale: Int) =
    this.toBigDecimal().setScale(scale, RoundingMode.HALF_UP).toString()

fun <T : Day> solve(
    enablePartOne: Boolean = true,
    enablePartTwo: Boolean = true,
    clazz: Class<T>,
) {
    val day = clazz.kotlin.createInstance()

    val partOneNanos = if (enablePartOne) {
        measureNanoTime { day.solvePart1() }
    } else null

    val partTwoNanos = if (enablePartTwo) {
        measureNanoTime { day.solvePart2() }
    } else null

    println()
    partOneNanos?.logTime("solution 1")
    partTwoNanos?.logTime("solution 2")
}

fun <T : Day> timed(
    clazz: Class<T>,
) {
    val day = clazz.kotlin.createInstance()

    val times = 1_0_000
    val warmup = 1__000
    val partOneNanos = (0..times).map {
        measureNanoTime { day.solvePart1() }
    }.drop(warmup)

    val partTwoNanos = (0..times).map {
        measureNanoTime { day.solvePart2() }
    }.drop(warmup)

    println()
    times.print("measurements: ")
    partOneNanos.average().logTime("1, average")
    partOneNanos.sorted().elementAt(times / 2).logTime("1, median")
    partTwoNanos.average().logTime("2, average")
    partTwoNanos.sorted().elementAt(times / 2).logTime("2, median")
}

inline fun <reified T : Day> solve(
    enablePartOne: Boolean = true,
    enablePartTwo: Boolean = true
) {
    solve<T>(enablePartOne, enablePartTwo, T::class.java)
}

inline fun <reified T : Day> time() {
    timed<T>(T::class.java)
}

fun readLines(file: String): List<String> {
    return Files.readAllLines(File(file).toPath())
}

fun List<String>.toIntegers(): List<Int> = this.map { it.toInt() }
fun List<String>.toLongs(): List<Long> = this.map { it.toLong() }

fun List<String>.toBigIntegers(): List<BigInteger> = this.map { it.toBigInteger() }

fun List<String>.toDoubles(): List<Double> = this.map { it.toDouble() }

fun List<String>.parseWithRegex(regexString: String): List<MatchResult.Destructured> =
    this
        .mapNotNull(Regex(regexString)::matchEntire)
        .map { it.destructured }

fun List<String>.categorizeWithRegex(vararg regexes: String): List<List<MatchResult.Destructured>> =
    regexes
        .map {
            this.parseWithRegex(it)
        }

fun <E> List<String>.matchWithRegexAndParse(vararg matchers: Pair<Regex, (MatchResult.Destructured) -> E>): List<E> =
    this
        .map { line ->
            matchers
                .mapNotNull { (regex, parser) ->
                    val match = regex.matchEntire(line)
                    match?.destructured?.to(parser)
                }
                .map { (match, parser) -> parser(match) }
                .first()
        }

fun <E> List<String>.matchAndParse(vararg matchers: Pair<String, (MatchResult.Destructured) -> E>): List<E> =
    this.matchWithRegexAndParse(*matchers.map { Regex(it.first) to it.second }.toTypedArray())

fun <E> List<E>.pairWithIndex(indexer: (index: Int) -> Int): List<Pair<E, E>> =
    this.mapIndexed { index, elem -> elem to this[indexer(index) % this.size] }

fun <E> List<E>.pairWithIndexAndSize(indexer: (index: Int, size: Int) -> Int): List<Pair<E, E>> =
    this.mapIndexed { index, elem -> elem to this[indexer(index, this.size) % this.size] }

fun <T> T.print(name: String) = this.also { println("$name: $it") }

fun <T> T.solution(part: Int) {
    this.print("Solution $part")
    if (Context.overwritingSolutions)
        saveSolution(part, this.toString())

    if (Context.testMode)
        checkSolution(part, this.toString())
}

fun <E, F> Sequence<E>.allPairings(with: Iterable<F>): Sequence<Pair<E, F>> {
    return this
        .flatMap { first ->
            with
                .asSequence()
                .map { first to it }
        }
}

fun <E, F> Iterable<E>.allPairings(with: Iterable<F>): Sequence<Pair<E, F>> {
    return this
        .asSequence()
        .flatMap { first ->
            with
                .asSequence()
                .map { first to it }
        }
}

fun <E> List<E>.allPairings(
    includeSelf: Boolean = false,
    bothDirections: Boolean = true
): Sequence<Pair<E, E>> {
    return this
        .asSequence()
        .mapIndexed { index, it ->
            val others = if (bothDirections)
                this
            else
                this.subList(index, this.size)

            others.mapNotNull { other ->
                if (it != other || includeSelf)
                    it to other
                else null
            }
        }
        .flatten()
}

fun <E> List<E>.allCombinations(): Sequence<List<E>> {
    if (this.isEmpty()) return sequenceOf(this)

    return this
        .drop(1)
        .allCombinations()
        .flatMap {
            sequenceOf(
                it + this.first(),
                it
            )
        }
}

fun <E> List<E>.permutations(): Sequence<List<E>> {
    if (this.size <= 1) return sequenceOf(this)
    val insertInto = this.drop(1)
        .permutations()

    return insertInto
        .flatMap { list ->
            (
                list.indices +
                    list.size
                )
                .asSequence()
                .map { list.take(it) + this.first() + list.drop(it) }
        }
}

fun <Node> dijkstraBigDecimal(
    start: Node,
    end: Node,
    getNeighbors: (from: Node) -> List<Node>,
    getWeightBetweenNodes: (from: Node, to: Node) -> BigDecimal
): Pair<List<Node>, BigDecimal> {
    return dijkstra(start, end, 0.toBigDecimal(), BigDecimal::plus, getNeighbors, getWeightBetweenNodes)
}

fun <Node> dijkstraDouble(
    start: Node,
    end: Node,
    getNeighbors: (from: Node) -> List<Node>,
    getWeightBetweenNodes: (from: Node, to: Node) -> Double
): Pair<List<Node>, Double> {
    return dijkstra(start, end, 0.0, Double::plus, getNeighbors, getWeightBetweenNodes)
}

fun <Node> dijkstraInt(
    start: Node,
    end: Node,
    getNeighbors: (from: Node) -> List<Node>,
    getWeightBetweenNodes: (from: Node, to: Node) -> Int
): Pair<List<Node>, Int> {
    return dijkstra(start, end, 0, Int::plus, getNeighbors, getWeightBetweenNodes)
}

fun <Node> dijkstraLong(
    start: Node,
    end: Node,
    getNeighbors: (from: Node) -> List<Node>,
    getWeightBetweenNodes: (from: Node, to: Node) -> Long
): Pair<List<Node>, Long> {
    return dijkstra(start, end, 0L, Long::plus, getNeighbors, getWeightBetweenNodes)
}

fun <Node, Weight> dijkstra(
    start: Node,
    end: Node,
    zero: Weight,
    add: (a: Weight, b: Weight) -> Weight,
    getNeighbors: (from: Node) -> List<Node>,
    getWeightBetweenNodes: (from: Node, to: Node) -> Weight
): Pair<List<Node>, Weight> where Weight : Number, Weight : Comparable<Weight> {
    val queue = PriorityQueue<Pair<List<Node>, Weight>>(Comparator.comparing { it.second })

    val seen = mutableSetOf(start)
    queue.add(listOf(start) to zero)

    while (queue.isNotEmpty()) {
        val next = queue.remove()

        if (next.first.last() == end) {
            return next
        }
        getNeighbors(next.first.last())
            .filter { it !in seen }
            .forEach {
                seen.add(it)
                queue.add(
                    (next.first + it) to (
                        add(
                            next.second,
                            getWeightBetweenNodes(next.first.last(), it)
                        )
                        )
                )
            }
    }
    error("no path  found!")
}

fun hammingDistance(a: String, b: String) =
    a.zip(b)
        .count { (a, b) -> a != b } +
        a.length - b.length

fun <E> LinkedList<E>.rotateRight(by: Int) {
    when {
        by < 0 ->
            repeat(-by) {
                this.addFirst(this.removeLast())
            }
        by > 0 ->
            repeat(by) {
                this.addLast(this.removeFirst())
            }
    }
}

val alphabet = ('a'..'z').toList()
val alphabetString = alphabet.joinToString("")

val uppercaseAlphabet = alphabet.map(Char::uppercaseChar)
val uppercaseAlphabetString = uppercaseAlphabet.joinToString("")

fun Int.digits() = this.toString().map { it.toString().toInt() }
fun Long.digits() = this.toString().map { it.toString().toInt() }

fun List<Int>.toLong(): Long {
    return this.reversed()
        .mapIndexed{ index, it -> it * 10.0.pow(index).toLong()}
        .sum()
}

fun <E> List<E>.replace(index: Int, item: E): List<E> =
    this.take(index) + item + this.drop(index + 1)

fun Pair<Int, Int>.manhattenDistance(to: Pair<Int, Int> = 0 to 0) =
    abs(this.first - to.first) + abs(this.second - to.second)

fun Pair<Int, Int>.distance(to: Pair<Int, Int> = 0 to 0): Double {
    val x = (this.first - to.first).toDouble()
    val y = (this.second - to.second).toDouble()
    return sqrt(x * x + y * y)
}

fun Pair<Int, Int>.asRange() = this.first..this.second

fun List<Int>.asRange(): IntRange {
    assert(this.size == 2)
    return this[0]..this[1]
}

fun <T> List<T>.commonPrefix(with: List<T>): List<T> =
    this.zip(with)
        .takeWhile { (a, b) -> a == b }
        .map { (a, _) -> a }

fun <E> channelOf(vararg values: E): Channel<E> {
    val channel = Channel<E>(values.size)
    values.forEach { channel.trySendBlocking(it) }
    return channel
}

fun <E> List<E>.padStart(size: Int, padWith: E) = (0 until size - this.size).map { padWith } + this

fun <E> E.repeatToSequence(times: Int) = this.repeatToSequence(times.toLong())

fun <E> E.repeatToSequence(times: Long): Sequence<E> {
    return (0 until times)
        .asSequence()
        .map { this }
}

fun Iterable<String>.splitAtEmptyLine(): Iterable<Iterable<String>> {
    return this.splitAt { it == "" }
}

fun <T> Iterable<T>.splitBeforeEach(predicate: (T) -> Boolean): Iterable<Iterable<T>> {
    return this.fold(mutableListOf(mutableListOf<T>())) { lists, element ->
        if (predicate(element)) {
            lists.add(mutableListOf(element))
        } else {
            lists.last().add(element)
        }
        lists
    }
}
fun <T> Iterable<T>.splitAt(predicate: (T) -> Boolean): Iterable<Iterable<T>> {
    return this.fold(mutableListOf(mutableListOf<T>())) { lists, element ->
        if (predicate(element)) {
            lists.add(mutableListOf())
        } else {
            lists.last().add(element)
        }
        lists
    }
}

fun <T> List<T>.runsOfLength(length: Int): List<List<T>> {
    return this.mapIndexed { index, _ -> (this.drop(index) + this.takeLast(index)).take(length) }
}

fun <T> List<T>.rotateRight(by: Int = 1): List<T> {
    return this.takeLast(by % this.size) + this.dropLast(by % this.size)
}

fun <T> List<T>.rotateLeft(by: Int = 1): List<T> {
    return this.drop(by % this.size) + this.take(by % this.size)
}

fun <T> List<List<T>>.swapDimensions(): List<List<T>> {
    return this.first().indices.map { index -> this.map { it[index] } }
}

// from https://rosettacode.org/wiki/Least_common_multiple#Kotlin
fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

fun fastExp(aBase: Long, aExponent: Long, module: Long): Long {

    var base = aBase
    var exponent = aExponent
    var result = 1L
    if (exponent % 2 == 1L) {
        result = base
    }

    while (exponent != 0L) {
        exponent = exponent shr 1
        base = (base * base) % module
        if (exponent % 2 == 1L)
            result = (result * base) % module
    }
    return result
}
