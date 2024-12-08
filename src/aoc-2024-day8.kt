import kotlin.io.path.Path
import kotlin.io.path.useLines
import kotlin.math.max

fun main() {
    var rowCount = 0
    var columnCount = 0

    val antennasByFrequency = Path("src/aoc-2024-day8.txt")
        .useLines { lines ->
            lines
                .withIndex()
                .flatMap { iLine ->
                    iLine.value
                        .withIndex()
                        .map { iChar -> Pair(iChar.value, Position(iLine.index, iChar.index)) }
                }
                .onEach { (_, position) ->
                    val (row, column) = position
                    rowCount = max(rowCount, row + 1)
                    columnCount = max(columnCount, column + 1)
                }
                .filter { (frequency, _) -> frequency != '.' }
                .groupBy { it.first }
        }

    val allAntennaPairs = antennasByFrequency
        .flatMap { (_, positions) ->
            positions
                .withIndex()
                .flatMap { iFirstPosition ->
                    positions
                        .drop(iFirstPosition.index + 1)
                        .map {
                            Pair(iFirstPosition.value.second, it.second)
                        }
                }
        }

    val isInBoundsOfMap = isValidPosition(rowCount, columnCount)

    val antiNodes = allAntennaPairs
        .flatMap { (a, b) ->
            val vector = positionSubtract(b, a)
            sequenceOf(
                positionAdd(b, vector),
                positionSubtract(a, vector)
            )
        }
        .filter { isInBoundsOfMap(it) }
        .toSet()

    val part1 = antiNodes.count()
    println("2024 Day 8 Part 1: $part1")

    val antiNodesWithHarmonics = allAntennaPairs
        .flatMap { (a, b) ->
            val vector = positionSubtract(b, a)
            sequenceOf(
                generateSequence(b) { antiNode ->
                    positionAdd(antiNode, vector)
                }.takeWhile { antiNode -> isInBoundsOfMap(antiNode) },
                generateSequence(a) { antiNode ->
                    positionSubtract(antiNode, vector)
                }.takeWhile { antiNode -> isInBoundsOfMap(antiNode) }
            )
                .flatten()
        }
        .toSet()

    val part2 = antiNodesWithHarmonics.count()
    println("2024 Day 8 Part 2: $part2")
}