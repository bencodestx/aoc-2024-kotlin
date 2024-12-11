import kotlin.io.path.Path
import kotlin.io.path.readLines

private data class PositionHeight(val position: Position, val height: Int)

private tailrec fun reachablePeaks(
    starts: Set<PositionHeight>,
    at: (Position) -> PositionHeight?
): Set<PositionHeight> {
    val stepsUp = starts
        .flatMap { from ->
            listOf(::up, ::down, ::left, ::right)
                .mapNotNull { at(it(from.position)) }
                .filter { it.height == from.height + 1 }
        }
        .toSet()

    if (stepsUp.isEmpty() || starts.first().height == 8) {
        return stepsUp
    } else {
        return reachablePeaks(stepsUp, at)
    }
}

private fun distinctHikingTrails(
    from: PositionHeight,
    at: (Position) -> PositionHeight?
): Int =
    listOf(::up, ::down, ::left, ::right)
        .mapNotNull { at(it(from.position)) }
        .filter { it.height == from.height + 1 }
        .sumOf { if (it.height == 9) 1 else distinctHikingTrails(it, at) }

fun main() {
    val lines = Path("src/aoc-2024-day10.txt").readLines()

    val trailheads = lines
        .flatMapIndexed { row, line ->
            line.mapIndexed { column, char -> PositionHeight(Position(row, column), char.toString().toInt()) }
        }
        .filter { location -> location.height == 0 }

    val isOnMap = isValidPosition(lines.size, lines[0].length)

    val at = { position: Position ->
        if (isOnMap(position)) PositionHeight(
            position,
            lines[position.first][position.second].toString().toInt()
        ) else null
    }

    val part1 =
        trailheads
            .flatMap { trailhead ->
                reachablePeaks(setOf(trailhead), at)
                    .map { peak -> Pair(trailhead, peak) }
            }
            .groupingBy { it.first }
            .eachCount()
            .values
            .sum()
    println("2024 Day 10 Part 1: $part1")

    val part2 = trailheads.sumOf { distinctHikingTrails(it, at) }
    println("2024 Day 10 Part 2: $part2")
}
