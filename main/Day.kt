package me.reckter.aoc

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.nio.file.Files

val client = OkHttpClient()
interface Day {

    val day: Int

    fun solvePart1()

    fun solvePart2()

    fun loadInput(part: Int = 0, trim: Boolean = true): List<String> {

        if (!File("input").exists()) {
            File("input").mkdir()
        }

        if (part != 0)
            return readLines("input/${day}_$part.txt")

        if (!Files.exists(File("input/$day.txt").toPath())) {
            println("downloading file for input $day...")

            val sessionFile = File("session-id.txt")
            if (!sessionFile.exists()) {
                error("input not there and could not download file, because session-id.txt is missing!")
            }

            val req = Request.Builder()
                .url("https://adventofcode.com/2021/day/$day/input")
                .addHeader("cookie", "session=${sessionFile.readText().trim()}")
                .build()
            val res = client.newCall(req).execute()

            if (!res.isSuccessful) {
                error("Failed to download part=$part!: ${res.body?.string()}")
            }

            val content = res.body?.string() ?: error("Got null response")
            File("input/$day.txt").writeText(content)
            println("download done")
        }

        return readLines("input/$day.txt")
            .let {
                if (trim) {
                    it.filter { it.isNotBlank() }
                } else
                    it
            }
    }
}
