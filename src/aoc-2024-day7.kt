import kotlin.io.path.Path
import kotlin.io.path.readLines

val equation_regex = Regex("""(\d+):\s*([\d\s]+)""")

typealias Numbers = List<Long>
typealias Equation = Pair<Long, Numbers>

typealias Operation = (Long, Long) -> Long

fun add(a: Long, b: Long) = a + b
fun multiply(a: Long, b: Long) = a * b
fun concatenate(a: Long, b: Long) = (a.toString() + b.toString()).toLong()

val part1Operations = listOf(::add, ::multiply)
val part2Operations = part1Operations + listOf(::concatenate)

fun possibleOperations(n: Int, operations: List<Operation>): List<List<Operation>> =
    if (n == 1) {
        operations.map { listOf(it) }
    } else {
        possibleOperations(n - 1, operations)
            .flatMap { previousOps -> operations.map { previousOps + listOf(it) } }
    }

fun calculate(numbers: Numbers, operations: List<Operation>) =
    numbers
        .drop(1)
        .zip(operations)
        .fold(numbers[0]) { a, (b, op) -> op(a, b) }

fun canSolve(equation: Equation, operations: List<Operation>) =
    possibleOperations(equation.second.size, operations)
        .any { opList -> calculate(equation.second, opList) == equation.first }

fun main() {
    val equations = Path("src/aoc-2024-day7.txt")
        .readLines()
        .map { line -> Pair(line, equation_regex.matchEntire(line)) }
        .map {
            val (line, match) = it
            require(match != null) { "Invalid equation line: '$line'" }
            Pair(
                match.groupValues[1].toLong(),
                match.groupValues[2].split(' ')
                    .map { number -> number.toLong() }
            )
        }

    val part1 = equations
        .filter { canSolve(it, part1Operations) }
        .sumOf { it.first }
    println("2024 Day 7 Part 1: $part1")

    val part2 = equations
        .filter { canSolve(it, part2Operations) }
        .sumOf { it.first }
    println("2024 Day 7 Part 2: $part2")
}