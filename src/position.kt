typealias Position = Pair<Int, Int>
typealias Movement = (Position) -> Position

fun right(position: Position) = Position(position.first, position.second + 1)
fun left(position: Position) = Position(position.first, position.second - 1)
fun up(position: Position) = Position(position.first - 1, position.second)
fun down(position: Position) = Position(position.first + 1, position.second)
fun downRight(position: Position) = right(down(position))
fun downLeft(position: Position) = left(down(position))
fun upRight(position: Position) = right(up(position))
fun upLeft(position: Position) = left(up(position))

fun isValidPosition(rows: Int, cols: Int) = { position: Position ->
    position.first >= 0 && position.second >= 0 && position.first < rows && position.second < cols
}

fun positionAdd(a: Position, b:Position) = Position(a.first + b.first, a.second + b.second)
fun positionSubtract(a: Position, b: Position) = Position(a.first - b.first, a.second - b.second)
