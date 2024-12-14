fun main() {
    val buttonARegex = Regex("""Button A: X\+(\d+), Y\+(\d+)""")
    val buttonBRegex = Regex("""Button B: X\+(\d+), Y\+(\d+)""")
    val prizeRegex = Regex("""Prize: X=(\d+), Y=(\d+)""")

    val lines = generateSequence { readlnOrNull() }
        .filter { it.isNotBlank() }
        .toList()

    var ax = 0L
    var ay = 0L
    var bx = 0L
    var by = 0L

    val playGames = {
        solver: (Long, Long) -> Long ->
        lines
            .mapNotNull { line ->
                val a = buttonARegex.matchEntire(line)
                if (a != null) {
                    ax = a.groupValues[1].toLong()
                    ay = a.groupValues[2].toLong()
                    null
                } else {
                    val b = buttonBRegex.matchEntire(line)
                    if (b != null) {
                        bx = b.groupValues[1].toLong()
                        by = b.groupValues[2].toLong()
                        null
                    } else {
                        val prize = prizeRegex.matchEntire(line)
                        if (prize != null) {
                            solver(prize.groupValues[1].toLong(), prize.groupValues[2].toLong())
                        } else {
                            error("Line does not appear to be A, B or Prize: '$line'")
                        }
                    }
                }
            }
    }

    val playPart1 = { x: Long, y: Long ->
        // https://todd.ginsberg.com/post/advent-of-code/2024/day13/
        val determinant = ax * by - ay * bx
        val aPresses = (x * by - y * bx) / determinant
        val bPresses = (ax * y - ay * x) / determinant
        if (ax * aPresses + bx * bPresses == x && ay * aPresses + by * bPresses == y) {
            aPresses * 3 + bPresses
        } else {
            0
        }
    }

    val part1 = playGames(playPart1).sum()
    println("2024 Day 13 Part 1: $part1")

    val playPart2 = { x: Long, y: Long ->
        playPart1(x + 10000000000000L, y + 10000000000000L)
    }

    val part2 = playGames(playPart2).sum()
    println("2024 Day 13 Part 2: $part2")
}
