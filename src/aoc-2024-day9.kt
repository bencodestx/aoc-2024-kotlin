fun main() {
    val input = readln()
        .map { it.toString().toInt() }

    val blockAllocation = input
        .flatMapIndexed { index, length ->
            val isEven = 0 == index % 2
            if (isEven) {
                val fileId = index / 2
                (0..<length)
                    .map { fileId }
            } else {
                (0..<length)
                    .map { null }
            }
        }.toMutableList()

    val part1 = generateSequence {
        while (blockAllocation.isNotEmpty()) {
            if (blockAllocation.last() == null) {
                blockAllocation.removeLast()
            } else if (blockAllocation.first() == null) {
                blockAllocation.removeFirst()
                return@generateSequence blockAllocation.removeLast()
            } else {
                return@generateSequence blockAllocation.removeFirst()
            }
        }
        null
    }
        .mapIndexed { position, fileId -> position * fileId }
        .map { it.toLong() }
        .sum()

    println("2024 Day 9 Part 1: $part1")

    val filesAndFreeSpace = input
        .runningFold(Pair(0, 0)) { previous, length -> Pair(previous.first + previous.second, length) }
        .drop(1)
        .mapIndexed { index, location ->
            val isEven = 0 == index % 2
            if (isEven) {
                val fileId = index / 2
                Pair(fileId, location)
            } else {
                Pair(null, location)
            }
        }

    val files = filesAndFreeSpace
        .mapNotNull { maybeFile ->
            if (maybeFile.first == null || maybeFile.second.second == 0) null else Pair(
                maybeFile.first!!,
                maybeFile.second
            )
        }
    val freeSpace =
        filesAndFreeSpace
            .mapNotNull { (fileId, location) -> if (fileId == null) location else null }
            .toTypedArray()

    val part2 = files
        .reversed()
        .map { (fileId, location) ->
            val holeIndex = freeSpace.indexOfFirst { hole -> hole.first < location.first && hole.second >= location.second }
            if (holeIndex == -1) {
                Pair(fileId, location)
            } else {
                val freeSpaceToOccupy = freeSpace[holeIndex]
                freeSpace[holeIndex] =
                    Pair(freeSpaceToOccupy.first + location.second, freeSpaceToOccupy.second - location.second)
                Pair(fileId, Pair(freeSpaceToOccupy.first, location.second))
            }
        }
        .flatMap { (fileId, location) ->
            (location.first..<(location.first + location.second))
                .map { it * fileId }
                .map { it.toLong() }
        }
        .sum()

    println("2024 Day 9 Part 2: $part2")
}