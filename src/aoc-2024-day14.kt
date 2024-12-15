import kotlin.io.path.Path
import kotlin.io.path.bufferedWriter

private fun stepRobot(start: Pair<Position, Position>, space: Position): Pair<Position, Position> {
    var end = Position(start.first.first + start.second.first, start.first.second + start.second.second)
    if (end.first >= space.first) {
        end = Position(end.first % space.first, end.second)
    }
    if (end.second >= space.second) {
        end = Position(end.first, end.second % space.second)
    }
    while (end.first < 0) {
        end = Position(end.first + space.first, end.second)
    }
    while (end.second < 0) {
        end = Position(end.first, end.second + space.second)
    }
    return Pair(end, start.second)
}

private fun stepRobots(starts: List<Pair<Position, Position>>, space: Position) = starts.map { stepRobot(it, space) }

private tailrec fun moveRobots(
    starts: List<Pair<Position, Position>>,
    space: Position,
    n: Int
): List<Pair<Position, Position>> {
    val nextStep = stepRobots(starts, space)
    return if (n == 1) {
        nextStep
    } else {
        moveRobots(nextStep, space, n - 1)
    }
}

fun main() {
    val regex = Regex("""p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""")

    val space = Position(103, 101)

    val robotStarts = generateSequence { readlnOrNull() }
        .mapNotNull { regex.matchEntire(it) }
        .map {
            val position = Position(it.groupValues[2].toInt(), it.groupValues[1].toInt())
            val velocity = Position(it.groupValues[4].toInt(), it.groupValues[3].toInt())
            Pair(position, velocity)
        }
        .toList()

    val robotEnds = moveRobots(robotStarts, space, 100)

    val midpoints = Position(space.first / 2, space.second / 2)

    val part1 = robotEnds
        .map { it.first }
        .fold(listOf(0, 0, 0, 0)) {
            counts, robot ->
            val top = robot.first < midpoints.first
            val bottom = robot.first > midpoints.first
            val left = robot.second < midpoints.second
            val right = robot.second > midpoints.second
            if (top && left)
                listOf(counts[0] + 1, counts[1], counts[2], counts[3])
            else if (top && right)
                listOf(counts[0], counts[1] + 1, counts[2], counts[3])
            else if (bottom && right)
                listOf(counts[0], counts[1], counts[2] + 1, counts[3])
            else if (bottom && left)
                listOf(counts[0], counts[1], counts[2], counts[3] + 1)
            else counts
        }
        .fold(1, { accumulator, count -> accumulator * count })

    println("2024 Day 14 Part 1: $part1")

    val part2 = 6668;
    val eggPositions = moveRobots(robotStarts, space, part2).map { it.first }
    println("2024 Day 13 Part 2: $part2")

    (0..<space.first).map { row ->
        (0..<space.second)
            .map { column -> Position(row, column) }
            .forEach { pixel -> if (eggPositions.contains(pixel)) print('*') else print(' ')}
        print('\n')
    }
}
