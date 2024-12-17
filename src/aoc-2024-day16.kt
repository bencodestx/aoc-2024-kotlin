data class MazePosition(
    val position: Position, val direction: Movement
)

private val leastCostToEnd = HashMap<Position, Pair<Long, Movement>>()
private val explored = HashSet<Position>()

private fun findTheChar(lines: List<String>, char: Char): Position {
    return lines
        .withIndex()
        .firstNotNullOf { row ->
            row
                .value
                .withIndex()
                .map { Pair(Position(row.index, it.index), it.value) }
                .firstOrNull { col -> col.second == char }
        }.first
}

private fun charAt(lines: List<String>, position: Position) = lines[position.first][position.second]

fun main() {
    val lines = generateSequence { readlnOrNull() }.toList()

    val startPosition = findTheChar(lines, 'S')
    val endPosition = findTheChar(lines, 'E')

    listOf(::left, ::right, ::up, ::down)
        .filter { charAt(lines, it(endPosition)) != '#' }
        .forEach {
            leastCostToEnd[it(endPosition)] = Pair(1L, it)
        }
    explored.add(endPosition)

    while (!leastCostToEnd.contains(startPosition)) {
        val cheapestUnexploredPositions =
            leastCostToEnd
                .entries
                .filter { !explored.contains(it.key) }
                .minOfOrNull { it.value.first }

        check(null != cheapestUnexploredPositions) { "There should always be an unexplored until we explore the start" }

        leastCostToEnd
            .entries
            .filter { it.value.first == cheapestUnexploredPositions }
            .forEach {
                toMap ->
                // What are all the ways we can reach this without reversing or hitting a wall
                listOf(::left, ::right, ::up, ::down)
                    .map { MazePosition(it(toMap.key), it) }
                    .filter { charAt(lines, it.position) != '#' }
                    .filter { !explored.contains(it.position) }
                    .forEach {
                        if (it.direction == toMap.value.second) {
                            // Moving the same direction
                            leastCostToEnd[it.position] = Pair(toMap.value.first + 1L, it.direction)
                        } else {
                            // Have to turn and step
                            leastCostToEnd[it.position] = Pair(toMap.value.first + 1001L, it.direction)
                        }
                    }
                explored.add(toMap.key)
            }
    }

    val bestPathFromStart = leastCostToEnd[startPosition]!!
    val part1 = when (bestPathFromStart.second) {
        ::left -> bestPathFromStart.first
        ::right -> bestPathFromStart.first + 2000L
        else -> bestPathFromStart.first + 1000L
    }
    println("2024 Day 16 Part 1: $part1")

    val part2 = 0
    println("2024 Day 16 Part 2: $part2")
}
