import kotlin.io.path.Path
import kotlin.io.path.readText

fun isValid(report: List<Int>, rules: Map<Int, List<Int>>): Boolean {
    val visited = HashSet<Int>()
    return report
        .all { currentPage ->
            visited.add(currentPage)
            rules[currentPage]?.all { followerPage -> !visited.contains(followerPage) } ?: true
        }
}

tailrec fun fixedReport(report: List<Int>, rules: Map<Int, List<Int>>): List<Int> {
    val visitedAt = HashMap<Int, Int>()
    val indicesToSwap = report.withIndex().firstNotNullOfOrNull {
        val currentIndex = it.index
        val currentPage = it.value
        visitedAt[currentPage] = currentIndex
        rules[currentPage]?.firstNotNullOfOrNull { followerPage ->
            when (val visitedIndex = visitedAt[followerPage]) {
                null -> null
                else -> Pair(currentIndex, visitedIndex)
            }
        }
    }

    return when (indicesToSwap) {
        null -> report
        else -> {
            val tweakedList = report.toMutableList()
            tweakedList[indicesToSwap.first] = tweakedList[indicesToSwap.second].also {
                tweakedList[indicesToSwap.second] = tweakedList[indicesToSwap.first]
            }
            fixedReport(tweakedList, rules)
        }
    }
}

fun main() {
    val text = Path("src/aoc-2024-day5.txt").readText()

    val rules =
        Regex("""(\d+)\|(\d+)""")
            .findAll(text)
            .map { Pair(it.groupValues[1].toInt(), it.groupValues[2].toInt()) }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })

    val reports = Regex("""\d+(,\d+)+""")
        .findAll(text)
        .map { it.value }
        .map { it.split(',').map { num -> num.removePrefix(",").toInt() } }

    val part1 = reports
        .filter { report -> isValid(report, rules) }
        .sumOf { report -> report[report.size / 2] }
    println("2024 Day 5 Part 1: $part1")

    val part2 = reports
        .filter { report -> !isValid(report, rules) }
        .map { badReport -> fixedReport(badReport, rules) }
        .sumOf { fixedReport -> fixedReport[fixedReport.size / 2] }
    println("2024 Day 5 Part 2: $part2")
}