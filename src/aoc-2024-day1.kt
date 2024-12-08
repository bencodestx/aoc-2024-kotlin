import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs

fun main() {
    val lines = Path("src/aoc-2024-day1.txt")
        .readLines()

    val (left, right) = lines
        .map {
            val split = it.split(Regex("\\s+"))
            check(split.size == 2) { "Each line should contain two numerical values: '$it'" }
            Pair(split[0].toLong(), split[1].toLong())
        }
        .unzip()

    val part1 = left
        .sorted()
        .zip(right.sorted())
        .sumOf { abs(it.first - it.second) }
    println("2024 Day 1 Part 1: $part1")

    val occurrences = right
        .groupingBy { it }
        .eachCount()
    val part2 = left
        .sumOf { it * occurrences.getOrDefault(it, 0) }
    println("2024 Day 1 Part 2: $part2")
}