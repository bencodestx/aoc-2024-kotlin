import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {
    val memory = Path("src/aoc-2024-day3.txt")
        .readText()

    val part1 = Regex("""mul\((\d+),(\d+)\)""")
        .findAll(memory)
        .map { mul(it) }
        .sum()
    println("2024 Day 3 Part 1: $part1")

    var enabled = true
    val part2 = Regex("""do\(\)|don't\(\)|mul\((\d+),(\d+)\)""")
        .findAll(memory)
        .map {
            when (it.value) {
                "do()" -> {
                    enabled = true
                    0
                }

                "don't()" -> {
                    enabled = false
                    0
                }

                else -> if (enabled) mul(it) else 0L
            }
        }
        .sum()
    println("2024 Day 3 Part 2: $part2")
}

private fun mul(it: MatchResult) = it.groupValues[1].toLong() * it.groupValues[2].toLong()