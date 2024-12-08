import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.max
import kotlin.math.min

fun main() {
    val reports = Path("src/aoc-2024-day2.txt")
        .readLines()
        .map { line ->
            line
                .split(' ')
                .map { it.toLong() }
        }

    val part1 = reports.count { report -> isValid(report) }
    println("2024 Day 2 Part 1: $part1")

    val part2 = reports.count { report -> isValid(report) || isValidWithSkip(report) }
    println("2024 Day 2 Part 2: $part2")
}

private fun isValid(report: List<Long>): Boolean {
    val diffs = report
        .windowed(2)
        .map { it[1] - it[0] }

    val (min, max) = diffs
        .drop(1)
        .fold(Pair(diffs[0], diffs[0])) { (prevMin, prevMax), it -> Pair(min(prevMin, it), max(prevMax, it)) }

    return (min >= 1 && max <= 3) || (min >= -3 && max <= -1)
}

private fun isValidWithSkip(report: List<Long>) =
    report
        .indices
        .map { indexToSkip -> report.filterIndexed { index, _ -> index != indexToSkip } }
        .any { isValid(it) }
