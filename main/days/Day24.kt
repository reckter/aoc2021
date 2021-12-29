package me.reckter.aoc.days

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.reckter.aoc.Day
import me.reckter.aoc.digits
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitBeforeEach
import me.reckter.aoc.toLong
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.pow

typealias State = Map<String, Int>

class Day24 : Day {
    override val day = 24

    data class Instruction(
        val instruction: Type,
        val dest: String,
        val src: String?
    ) {
        enum class Type {
            inp,
            add,
            mul,
            div,
            mod,
            eql
        }
    }

    data class State(
        val register: Map<String, Int>,
        val input: List<Int>
    )

    fun State.getValue(regOrIm: String): Int {
        return this.register[regOrIm] ?: regOrIm.toInt()
    }

    fun State.runInstruction(instruction: Instruction): State {
        return when (instruction.instruction) {
            Instruction.Type.inp -> State(
                register + (instruction.dest to input.first()),
                input.drop(1)
            )
            Instruction.Type.add -> this.copy(
                register = register + (instruction.dest to (getValue(
                    instruction.dest
                ) + getValue(instruction.src!!)))
            )
            Instruction.Type.mul -> this.copy(
                register = register + (instruction.dest to (getValue(
                    instruction.dest
                ) * getValue(instruction.src!!)))
            )
            Instruction.Type.div -> this.copy(
                register = register + (instruction.dest to (getValue(
                    instruction.dest
                ) / getValue(instruction.src!!)))
            )
            Instruction.Type.mod -> this.copy(
                register = register + (instruction.dest to (getValue(
                    instruction.dest
                ) % getValue(instruction.src!!)))
            )
            Instruction.Type.eql -> this.copy(
                register = register + (instruction.dest to (if (getValue(
                        instruction.dest
                    ) == getValue(instruction.src!!)
                ) 1 else 0))
            )
        }
    }

    fun runProgram(instruction: List<Instruction>, input: List<Int>): State {
        return instruction.fold(
            State(
                mapOf("w" to 0, "x" to 0, "y" to 0, "z" to 0),
                input
            )
        ) { state, instr ->
            state.runInstruction(instr)
        }
    }

    fun State.runProgram(instruction: List<Instruction>): State {
        return instruction.fold(
            this
        ) { state, instr ->
            state.runInstruction(instr)
        }
    }

    val cache = mutableMapOf<Pair<Int, State>, List<Int>?>()

    fun State.getRestOfSequence(
        partsLeft: List<List<Instruction>>,
        getBiggest: Boolean = true
    ): List<Int>? {

        val key = partsLeft.size to this

        if (key in cache) return cache[key]

        if (this.register["z"]!! > 26.0.pow(
                min(
                    3,
                    partsLeft.size
                )
            )
        )
            return null

        if (partsLeft.isEmpty()) {
            if (this.register["z"] == 0) return emptyList()
            return null
        }

        val ret = (9 downTo 1)
            .asSequence()
            .map {
                val state = this.copy(input = listOf(it)).runProgram(partsLeft.first())

                it to state
            }
            .sortedBy { it.second.register["z"]!! }
            .mapNotNull { (it, state) ->
                val result =
                    state.getRestOfSequence(partsLeft.drop(1), getBiggest) ?: return@mapNotNull null

                it to listOf(it) + result
            }
            .sortedByDescending { (if (getBiggest) 1 else -1) * it.first }
            .firstOrNull()
            ?.second

        cache[key] = ret
        return ret
    }

    val instructions by lazy {
        loadInput()
            .map {
                val parts = it.split(" ")
                val type = Instruction.Type.values().find { it.toString() == parts.first() }!!
                val dest = parts[1]
                val src = parts.getOrNull(2)
                Instruction(type, dest, src)
            }
    }

    val parts by lazy {
        instructions.splitBeforeEach { it.instruction == Instruction.Type.inp }
            .drop(1)
            .map { it.toList() }
    }

    override fun solvePart1() {
        cache.clear()
        State(
            mapOf("w" to 0, "x" to 0, "y" to 0, "z" to 0),
            emptyList()
        )
            .getRestOfSequence(parts)
            ?.toLong()
            .solution(1)
    }

    override fun solvePart2() {
        cache.clear()
        State(
            mapOf("w" to 0, "x" to 0, "y" to 0, "z" to 0),
            emptyList()
        )
            .getRestOfSequence(parts, false)
            ?.toLong()
            .solution(2)
    }
}

fun main() = solve<Day24>()
