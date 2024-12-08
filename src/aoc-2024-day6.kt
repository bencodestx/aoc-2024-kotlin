import kotlin.io.path.Path
import kotlin.io.path.readLines

private typealias State = Pair<Position, Movement>

fun nextDirection(currentDirection: Movement): Movement {
    return when (currentDirection) {
        ::up -> ::right
        ::right -> ::down
        ::down -> ::left
        ::left -> ::up
        else -> error("Unexpected direction")
    }
}

fun main() {
    val lines = Path("src/aoc-2024-day6.txt")
        .readLines()

    val rows = lines.size
    val columns = lines[0].length
    val isInsideLab = isValidPosition(rows, columns)
    val initialRow = lines.indexOfFirst { it.contains('^') }
    val initialColumn = lines[initialRow].indexOf('^')
    val initialPosition = Position(initialRow, initialColumn)

    val lab = lines.map { it.toCharArray() }

    lab[initialRow][initialColumn] = 'X'

    val isObstacle = { position: Position ->
        lab[position.first][position.second] == '#'
    }

    generateSequence(State(initialPosition, ::up)) { (currentPosition, currentDirection) ->
        val newPosition = currentDirection(currentPosition)
        if (isInsideLab(newPosition)) {
            if (isObstacle(newPosition)) {
                val newDirection = nextDirection(currentDirection)
                State(currentPosition, newDirection)
            } else {
                lab[newPosition.first][newPosition.second] = 'X'
                State(newPosition, currentDirection)
            }
        } else {
            null
        }
    }.forEach { _ -> }

    val part1 = lab.sumOf { line ->
        line.count { char -> char == 'X' }
    }
    println("2024 Day 6 Part 1: $part1")

    val allRows = 0..<rows
    val allColumns = 0..<columns
    val allPossibleObstaclePositions = allRows
        .flatMap { row -> allColumns.map { col -> Position(row, col) } }
        .filter { it != initialPosition }
        .filter { !isObstacle(it) }

    val part2 = allPossibleObstaclePositions
        .map { obstaclePosition ->
            val visitedStates = HashSet<State>()
            generateSequence(State(initialPosition, ::up)) { (currentPosition, currentDirection) ->
                val newPosition = currentDirection(currentPosition)
                if (isInsideLab(newPosition)) {
                    if (isObstacle(newPosition) || newPosition == obstaclePosition) {
                        val newDirection = nextDirection(currentDirection)
                        State(currentPosition, newDirection)
                    } else {
                        State(newPosition, currentDirection)
                    }
                } else {
                    null
                }
            }
                .any {
                    !visitedStates.add(it)
                }
        }
        .count { it }
    println("2024 Day 6 Part 2: $part2")
}