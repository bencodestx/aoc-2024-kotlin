fun main() {
    val lines = generateSequence { readlnOrNull() }.toList()
    val isInGarden = isValidPosition(lines.size, lines[0].length)

    val plantPositions = lines
        .asSequence()
        .flatMapIndexed { row, line ->
            line.mapIndexed { column, region -> Pair(Position(row, column), region) }
        }
        .groupBy { it.second }

    val allRegions = HashMap<Char, ArrayList<List<Position>>>()
    plantPositions
        .forEach { (plant, list) ->
            val neighbors = HashSet<Position>()
            val unassigned = list.map { it.first }.toMutableSet()
            while (unassigned.isNotEmpty()) {
                val preCount = neighbors.size

                if (neighbors.isEmpty()) {
                    neighbors.add(unassigned.first())
                    unassigned.remove(neighbors.first())
                }

                neighbors
                    .toList()
                    .forEach { possible ->
                        listOf(::left, ::right, ::up, ::down)
                            .map { movement -> movement(possible) }
                            .filter { isInGarden(it) }
                            .filter { lines[it.first][it.second] == plant }
                            .forEach { neighbors.add(it) }
                    }
                val postCount = neighbors.size

                unassigned.removeIf { neighbors.contains(it) }

                if (unassigned.isEmpty() || (preCount == postCount)) {
                    allRegions.getOrPut(plant, { ArrayList() }).add(neighbors.toList())
                    neighbors.clear()
                }
            }
        }

    val area = { region: List<Position> -> region.size }

    val perimeter = { plant: Char, region: List<Position> ->
        region.flatMap {
            listOf(::right, ::left, ::up, ::down)
                .map { movement -> movement(it) }
        }
            .count { (!isInGarden(it)) || (lines[it.first][it.second] != plant) }
    }

    val horizontalSides = { plant: Char, region: List<Position> ->
        val uniqueRows = region.map { it.first }.toSet()
        uniqueRows.sumOf { row ->
            listOf(::up, ::down)
                .map { movement ->
                    region
                        .asSequence()
                        .filter { it.first == row }
                        .filter {
                            val newPosition = movement(it)
                            (!isInGarden(newPosition)) || (lines[newPosition.first][newPosition.second] != plant)
                        }
                        .map { it.second }
                        .sorted()
                        .toList()
                }
                .filter { it.isNotEmpty() }
                .sumOf { list ->
                    1 + list.windowed(2).count { (x, y) -> x + 1 != y }
                }
        }
    }

    val verticalSides = { plant: Char, region: List<Position> ->
        val uniqueColumns = region.map { it.second }.toSet()
        uniqueColumns.sumOf { column ->
            listOf(::left, ::right)
                .map { movement ->
                    region
                        .asSequence()
                        .filter { it.second == column }
                        .filter {
                            val newPosition = movement(it)
                            (!isInGarden(newPosition)) || (lines[newPosition.first][newPosition.second] != plant)
                        }
                        .map { it.first }
                        .sorted()
                        .toList()
                }
                .filter { it.isNotEmpty() }
                .sumOf { list ->
                    1 + list.windowed(2).count { (x, y) -> x + 1 != y }
                }
        }
    }

    val sides = { plant: Char, region: List<Position> -> verticalSides(plant, region) + horizontalSides(plant, region) }

    val flatRegions =
        allRegions
            .entries
            .flatMap { (plant, allPositions) -> allPositions.map { Pair(plant, it) } }


    val part1 =
        flatRegions
            .sumOf { (plant, positions) ->
                area(positions) * perimeter(plant, positions)
            }
    println("2024 Day 12 Part 1: $part1")

    val part2 =
        flatRegions
            .asSequence()
            .map { (plant, positions) ->
                area(positions) * sides(plant, positions)
            }
            .sum()
    println("2024 Day 12 Part 2: $part2")
}
