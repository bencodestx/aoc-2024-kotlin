private val memory = mutableMapOf<Pair<Long, Int>, Long>()

private fun howManyStones(stone: Long, blinks: Int): Long =
    memory.getOrPut(Pair(stone, blinks)) {
        if (blinks == 0)
            1
        else if (stone == 0L)
            howManyStones(1L, blinks - 1)
        else {
            val digits = stone.toString()
            val digitCount = digits.length
            val isEven = digitCount % 2 == 0
            if (isEven) {
                val halfDigits = digitCount / 2
                howManyStones(digits.take(halfDigits), blinks - 1) + howManyStones(digits.drop(halfDigits), blinks - 1)
            } else {
                howManyStones(stone * 2024L, blinks - 1)
            }
        }
    }

private fun howManyStones(stone: String, blinks: Int): Long = howManyStones(stone.toLong(), blinks)

fun main() {
    val initialStones = readln().split(' ')

    val part1 = initialStones.sumOf { howManyStones(it, 25) }
    println("2024 Day 11 Part 1: $part1")

    val part2 = initialStones.sumOf { howManyStones(it, 75) }
    println("2024 Day 11 Part 2: $part2")
}
