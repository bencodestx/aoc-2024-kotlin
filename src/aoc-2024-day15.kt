private fun movement(char: Char) = when (char) {
    'v' -> ::down
    '^' -> ::up
    '<' -> ::left
    '>' -> ::right
    else -> throw Exception("Unknown movement '$char'")
}

private fun applyMove(
    warehouse: Array<CharArray>,
    start: Position,
    move: Movement
): Boolean {
    val end = move(start)
    val atEnd = warehouse[end.first][end.second]
    return if ('#' == atEnd) {
        false
    } else if ('.' == atEnd || applyMove(warehouse, end, move)) {
        warehouse[end.first][end.second] = warehouse[start.first][start.second]
        warehouse[start.first][start.second] = '.'
        true
    } else {
        false
    }
}

private fun canApplyWideHorizontalMove(
    warehouse: Array<CharArray>,
    start: Position,
    move: Movement
): Boolean {
    val end = move(start)
    val atEnd = warehouse[end.first][end.second]
    return when (atEnd) {
        '#' -> false
        '.' -> true
        else -> canApplyWideHorizontalMove(warehouse, end, move)
    }
}

private fun applyWideHorizontalMove(
    warehouse: Array<CharArray>,
    start: Position,
    move: Movement
) {
    val end = move(start)
    val atEnd = warehouse[end.first][end.second]
    if ('.' != atEnd) {
        applyWideHorizontalMove(warehouse, end, move)
    }
    warehouse[end.first][end.second] = warehouse[start.first][start.second]
    warehouse[start.first][start.second] = '.'
}

private fun canApplyWideVerticalMove(
    warehouse: Array<CharArray>,
    start: Position,
    move: Movement
): Boolean {
    val end = move(start)
    return when (val atEnd = warehouse[end.first][end.second]) {
        '#' -> false
        '.' -> true
        '[' -> canApplyWideVerticalMove(warehouse, end, move)
                    && canApplyWideVerticalMove(warehouse, right(end), move)
        ']' -> canApplyWideVerticalMove(warehouse, end, move)
                    && canApplyWideVerticalMove(warehouse, left(end), move)
        else -> throw Exception("Unexpected move target '$atEnd'")
    }
}

private fun applyWideVerticalMove(
    warehouse: Array<CharArray>,
    start: Position,
    move: Movement
) {
    val end = move(start)
    val atEnd = warehouse[end.first][end.second]
    if ('[' == atEnd) {
        applyWideVerticalMove(warehouse, end, move)
        applyWideVerticalMove(warehouse, right(end), move)
    } else if (']' == atEnd) {
        applyWideVerticalMove(warehouse, end, move)
        applyWideVerticalMove(warehouse, left(end), move)
    } else if ('.' != atEnd) {
        throw Exception("Unexpected move target '$atEnd'")
    }

    warehouse[end.first][end.second] = warehouse[start.first][start.second]
    warehouse[start.first][start.second] = '.'
}

private fun applyWideMove(
    warehouse: Array<CharArray>,
    start: Position,
    move: Movement
) = if (move == ::left || move == ::right) {
    if (canApplyWideHorizontalMove(warehouse, start, move)) {
        applyWideHorizontalMove(warehouse, start, move)
        move(start)
    } else {
        start
    }
} else {
    if (canApplyWideVerticalMove(warehouse, start, move)) {
        applyWideVerticalMove(warehouse, start, move)
        move(start)
    } else {
        start
    }
}

fun main() {
    val warehouseRegex = Regex("""#[#O.@]+#""")
    val movesRegex = Regex("""[<>v^]+""")

    val input = generateSequence { readlnOrNull() }.toList()

    val warehouse = input
        .mapNotNull { warehouseRegex.matchEntire(it) }
        .map { it.value.toCharArray() }
        .toTypedArray()

    val moves = input
        .mapNotNull { movesRegex.matchEntire(it) }
        .joinToString(separator = "") { it.value }

    val robotStart = warehouse
        .mapIndexedNotNull { row, text ->
            val column = text.indexOf('@')
            if (column == -1) {
                null
            } else {
                Position(row, column)
            }
        }
        .first()

    moves
        .map { movement(it) }
        .fold(robotStart) { robotPosition, move ->
            val target = move(robotPosition)
            if (applyMove(warehouse, robotPosition, move)) {
                target
            } else {
                robotPosition
            }
        }

    val part1 = warehouse
        .flatMapIndexed { row, text ->
            text
                .mapIndexed { column, c -> if (c == 'O') Position(row, column) else null }
                .filterNotNull()
                .map { it.first * 100 + it.second }
        }
        .sum()
    println("2024 Day 15 Part 1: $part1")

    val wideWarehouse = input
        .mapNotNull { warehouseRegex.matchEntire(it) }
        .map {
            it
                .value
                .asSequence()
                .map { char ->
                    when (char) {
                        '#' -> "##"
                        'O' -> "[]"
                        '.' -> ".."
                        '@' -> "@."
                        else -> throw Exception("Unknown warehouse character '$it'")
                    }
                }
                .joinToString("")
                .toCharArray()
        }
        .toTypedArray()

    val wideRobotStart = wideWarehouse
        .mapIndexedNotNull { row, text ->
            val column = text.indexOf('@')
            if (column == -1) {
                null
            } else {
                Position(row, column)
            }
        }
        .first()

    moves
        .map { movement(it) }
        .fold(wideRobotStart) { robotPosition, move ->
            applyWideMove(wideWarehouse, robotPosition, move)
        }

    val part2 = wideWarehouse
        .flatMapIndexed { row, text ->
            text
                .mapIndexed { column, c -> if (c == '[') Position(row, column) else null }
                .filterNotNull()
                .map { it.first * 100 + it.second }
        }
        .sum()
    println("2024 Day 15 Part 2: $part2")
}
