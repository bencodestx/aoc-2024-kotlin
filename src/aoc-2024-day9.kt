import kotlin.io.path.Path
import kotlin.io.path.readText

data class File(val fileId: Long, val position: Long, val length: Long, val isMoved: Boolean = false)

fun main() {
    var currentPosition: Long = 0
    val input = ArrayList<File>()
    Path("src/aoc-2024-day9.txt")
        .readText()
        .filter { it.isDigit() }
        .map { it.toString().toLong() }
        .forEachIndexed { index, length ->
            val filePosition = currentPosition
            currentPosition += length
            if (0 == index % 2) {
                input.add(File(fileId = index.toLong() / 2, position = filePosition, length = length))
            }
        }

    val part1ResultingFiles = ArrayList<File>()
    part1ResultingFiles.add(input.first())
    val part1RemainingInput = input.drop(1).toMutableList()
    while (part1RemainingInput.isNotEmpty()) {
        val prev = part1ResultingFiles.last()
        val nextPosition = prev.position + prev.length
        val contiguousFreeSpace = part1RemainingInput.first().position - nextPosition
        if (contiguousFreeSpace > 0) {
            val last = part1RemainingInput.last()
            if (contiguousFreeSpace >= last.length) {
                // Move entire last file to output
                part1ResultingFiles.add(File(fileId = last.fileId, position = nextPosition, length = last.length))
                part1RemainingInput.removeLast()
            } else {
                // Move part of last file to output
                part1ResultingFiles.add(
                    File(
                        fileId = last.fileId,
                        position = nextPosition,
                        length = contiguousFreeSpace
                    )
                )
                part1RemainingInput.removeLast()
                part1RemainingInput.add(
                    File(
                        fileId = last.fileId,
                        position = last.position,
                        length = last.length - contiguousFreeSpace
                    )
                )
            }
        } else {
            check(contiguousFreeSpace == 0L) { "How do we have negative free space? $contiguousFreeSpace" }
            // Move the first input to output
            part1ResultingFiles.add(part1RemainingInput.first())
            part1RemainingInput.removeFirst()
        }
    }

    val part1 = checksum(part1ResultingFiles)
    println("2024 Day 9 Part 1: $part1")

    val part2RemainingInput = input.toMutableList()
    val part2Output = ArrayList<File>()

    while (part2RemainingInput.size > 2) {
        val last = part2RemainingInput.last()
        val insertAfter = if (last.isMoved) null else part2RemainingInput
            .asSequence()
            .windowed(2)
            .map { (first, second) ->
                second.position - (first.position + first.length)
            }
            .onEach {
                check(it >= 0L) { "How do we have negative free space? $it" }
            }
            .withIndex()
            .firstOrNull { it.value >= last.length }
        if (insertAfter == null) {
            part2Output.add(last)
        } else {
            val fileToInsertAfter = part2RemainingInput[insertAfter.index]
            val newPosition = fileToInsertAfter.position + fileToInsertAfter.length
            part2RemainingInput.add(
                insertAfter.index + 1,
                File(fileId = last.fileId, position = newPosition, length = last.length, isMoved = true)
            )
        }
        part2RemainingInput.removeLast()
    }
    part2Output.addAll(part2RemainingInput)

    val part2 = checksum(part2Output)
    println("2024 Day 9 Part 2: $part2")
}

private fun checksum(files: ArrayList<File>) = files
    .flatMap { file ->
        (file.position..<file.position + file.length)
            .map { it * file.fileId }
    }
    .sum()