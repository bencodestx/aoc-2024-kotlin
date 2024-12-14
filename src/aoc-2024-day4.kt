import kotlin.io.path.Path
import kotlin.io.path.readLines

private val forwards = Regex("XMAS")
private val backwards = Regex("SAMX")

private fun countMatches(line: String) = forwards.findAll(line).count() + backwards.findAll(line).count()

private fun countMatches(lines: List<String>) = lines.sumOf { countMatches(it) }

private fun positionsFrom(startPosition: Position, isValid: (Position) -> Boolean, direction: Movement) =
    generateSequence(startPosition, direction).takeWhile { isValid(it) }

private fun stringFrom(lines: List<String>, startPosition: Position, direction: (Position) -> Position) =
    positionsFrom(startPosition, isValidPosition(lines.size, lines[0].length), direction)
        .map { position -> lines[position.first][position.second] }
        .fold("") { acc, char -> acc + char }

fun main() {
    val lines = Path("src/aoc-2024-day4.txt")
        .readLines()

    val horizontalLines = lines.indices.map { stringFrom(lines, Position(it, 0), ::right) }
    val verticalLines = lines[0].indices.map { stringFrom(lines, Position(0, it), ::down) }
    val diagonalDownRightLinesFromTop = lines[0].indices.map { stringFrom(lines, Position(0, it), ::downRight) }
    val diagonalDownLeftLinesFromTop = lines[0].indices.map { stringFrom(lines, Position(0, it), ::downLeft) }
    val diagonalDownLeftLinesFromRight = lines.indices.drop(1).map { stringFrom(lines, Position(it, lines[0].length - 1), ::downLeft) }
    val diagonalDownRightLinesFromLeft = lines.indices.drop(1).map { stringFrom(lines, Position(it, 0), ::downRight) }

    val part1 = listOf(
        horizontalLines,
        verticalLines,
        diagonalDownRightLinesFromTop,
        diagonalDownLeftLinesFromTop,
        diagonalDownLeftLinesFromRight,
        diagonalDownRightLinesFromLeft)
        .sumOf { countMatches(it) }
    println("2024 Day 4 Part 1: $part1")

    val charAt = { position: Position -> lines[position.first][position.second] }

    val aRows = 1..<(lines.size - 1)
    val aCols = 1..<(lines[0].length - 1)

    val part2 = aRows.sumOf { row ->
        aCols
            .map { col -> Position(row, col) }
            .filter { position -> charAt(position) == 'A' }
            .map { position ->
                val ul = charAt(upLeft(position))
                val ur = charAt(upRight(position))
                val dr = charAt(downRight(position))
                val dl = charAt(downLeft(position))
                "$ul$ur$dr$dl"
            }
            .count { pattern ->
                when (pattern) {
                    "MMSS" -> true
                    "SSMM" -> true
                    "SMMS" -> true
                    "MSSM" -> true
                    else -> false
                }
            }
    }
    println("2024 Day 4 Part 2: $part2")
}