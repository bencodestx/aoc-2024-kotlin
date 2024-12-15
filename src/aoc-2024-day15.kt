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
    val oneAway = move(start)
    val atOneAway = warehouse[oneAway.first][oneAway.second]
    return when (atOneAway) {
        '#' -> false
        '.' -> true
        else -> {
            val twoAway = move(oneAway)
            val atTwoAway = warehouse[twoAway.first][twoAway.second]
            when (atTwoAway) {
                '#' -> false
                '.' -> true
                else -> canApplyWideHorizontalMove(warehouse, twoAway, move)
            }
        }
    }
}

private fun applyWideHorizontalMove(
    warehouse: Array<CharArray>,
    start: Position,
    move: Movement
) {
    val oneAway = move(start)
    val atOneAway = warehouse[oneAway.first][oneAway.second]
    if ('.' == atOneAway) {
        warehouse[oneAway.first][oneAway.second] = warehouse[start.first][start.second]
        warehouse[start.first][start.second] = '.'
    } else {
        val twoAway = move(oneAway)
        val atTwoAway = warehouse[twoAway.first][twoAway.second]
        if ('.' != atTwoAway) {
            applyWideHorizontalMove(warehouse, twoAway, move)
        }
        warehouse[twoAway.first][twoAway.second] = warehouse[oneAway.first][oneAway.second]
        warehouse[oneAway.first][oneAway.second] = warehouse[start.first][start.second]
        warehouse[start.first][start.second] = '.'
    }
}

private fun canApplyWideVerticalMove(
    warehouse: Array<CharArray>,
    start: Position,
    move: Movement
): Boolean {
    val oneAway = move(start)
    return when (val atOneAway = warehouse[oneAway.first][oneAway.second]) {
        '#' -> false
        '.' -> true
        '[' -> canApplyWideVerticalMove(warehouse, oneAway, move)
                    && canApplyWideVerticalMove(warehouse, right(oneAway), move)
        ']' -> canApplyWideVerticalMove(warehouse, oneAway, move)
                    && canApplyWideVerticalMove(warehouse, left(oneAway), move)
        else -> throw Exception("Unexpected move target '$atOneAway'")
    }
}

private fun applyWideVerticalMove(
    warehouse: Array<CharArray>,
    start: Position,
    move: Movement
) {
    val oneAway = move(start)
    val atOneAway = warehouse[oneAway.first][oneAway.second]
    if ('[' == atOneAway) {
        applyWideVerticalMove(warehouse, oneAway, move)
        applyWideVerticalMove(warehouse, right(oneAway), move)
    } else if (']' == atOneAway) {
        applyWideVerticalMove(warehouse, oneAway, move)
        applyWideVerticalMove(warehouse, left(oneAway), move)
    } else if ('.' != atOneAway) {
        throw Exception("Unexpected move target '$atOneAway'")
    }

    warehouse[oneAway.first][oneAway.second] = warehouse[start.first][start.second]
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
