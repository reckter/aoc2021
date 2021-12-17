package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day16 : Day {
    override val day = 16

    data class Packet(
        val version: Int,
        val type: Int,
        val subPackets: List<Packet>,
        val literalValue: Long?
    )

    fun List<Char>.toInt(): Int =
        this.joinToString("") { it.toString() }.toInt(2)

    private fun parseLiteral(bits: List<Char>, prefix: Long = 0): Pair<Long, List<Char>> {
        val number = bits.drop(1).take(4).toInt().toLong()
        val value = prefix * 16L + number
        if (bits.first() == '0') {
            return value to bits.drop(5)
        }

        return parseLiteral(bits.drop(5), value)
    }

    private fun parsePacket(bits: List<Char>): Pair<Packet, List<Char>> {
        val version = bits.take(3).toInt()
        val type = bits.drop(3).take(3).toInt()

        return when (type) {
            4 -> {
                // literal packet
                val (literal, rest) = parseLiteral(bits.drop(6))
                Packet(
                    version,
                    type,
                    emptyList(),
                    literal
                ) to rest
            }
            else -> {
                val lengthMode = bits.drop(6).take(1).toInt()
                val (rest, length) = when (lengthMode) {
                    1 -> bits.drop(3 + 3 + 1 + 11) to bits.drop(7).take(11).toInt()
                    0 -> bits.drop(3 + 3 + 1 + 15) to bits.drop(7).take(15).toInt()
                    else -> error("invalid length type $lengthMode")
                }

                val (subPackets, remainingBits) = when (lengthMode) {
                    1 -> {
                        var remainingBits = rest
                        val subPackets = (0 until length)
                            .map {
                                val (packet, bitsAfterParse) = parsePacket(remainingBits)
                                remainingBits = bitsAfterParse

                                packet
                            }

                        subPackets to remainingBits
                    }
                    0 -> {
                        var remainingBits = rest
                        val subPackets = mutableListOf<Packet>()
                        while (length > rest.size - remainingBits.size) {
                            val (packet, bitsAfterParse) = parsePacket(remainingBits)
                            remainingBits = bitsAfterParse
                            subPackets.add(packet)
                        }
                        subPackets to remainingBits
                    }
                    else -> error("invalid length type $lengthMode")
                }

                Packet(
                    version,
                    type,
                    subPackets,
                    null
                ) to remainingBits
            }
        }
    }

    private fun Packet.versionSum(): Int {
        return this.version + this.subPackets.sumOf { it.versionSum() }
    }

    private fun Packet.value(): Long {
        val subPacketValues = subPackets.map { it.value() }
        return when (this.type) {
            0 -> subPacketValues.sum()
            1 -> subPacketValues.reduce { acc, i -> acc * i }
            2 -> subPacketValues.minOrNull()!!
            3 -> subPacketValues.maxOrNull()!!
            4 -> this.literalValue!!
            5 -> if (subPacketValues[0] > subPacketValues[1]) 1 else 0
            6 -> if (subPacketValues[0] < subPacketValues[1]) 1 else 0
            7 -> if (subPacketValues[0] == subPacketValues[1]) 1 else 0
            else -> error("invalid type ${this.type}")
        }
    }

    private val packet by lazy {
        val bits = loadInput()
            .first()
            .split("")
            .filter { it.isNotBlank() }
            .map { it.toInt(16) }
            .flatMap { it.toString(2).padStart(4, '0').toCharArray().toList() }

        val (packet, rest) = parsePacket(bits)
        packet
    }
    override fun solvePart1() {
        packet.versionSum().solution(1)
    }

    override fun solvePart2() {
        packet.value().solution(2)
    }
}

fun main() = solve<Day16>()
