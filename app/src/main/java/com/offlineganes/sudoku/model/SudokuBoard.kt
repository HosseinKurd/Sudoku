package com.offlineganes.sudoku.model

data class SudokuBoard(val cells: List<List<SudokuCell>>) {

    // Constructor to create an empty board or a board from a given 2D array
    constructor(initialBoard: Array<IntArray>) : this(
        initialBoard.map { row ->
            row.map { value ->
                SudokuCell(value, isEditable = value == 0) // 0 means empty, hence editable
            }
        }
    )

    fun getCell(row: Int, col: Int): SudokuCell {
        require(row in 0..8 && col in 0..8) { "Invalid row or column index" }
        return cells[row][col]
    }

    // Creates a new SudokuBoard with the specified cell updated
    fun updateCell(row: Int, col: Int, newValue: Int): SudokuBoard {
        require(row in 0..8 && col in 0..8) { "Invalid row or column index" }
        require(newValue in 0..9) { "Cell value must be between 0 and 9" }

        val newCells = cells.toMutableList().map { it.toMutableList() }
        val currentCell = newCells[row][col]

        // Only update if the cell is editable
        if (currentCell.isEditable) {
            newCells[row][col] = currentCell.copy(value = newValue)
        }
        return SudokuBoard(newCells.map { it.toList() }) // Convert back to immutable lists
    }

    // Checks if the board is completely filled (no zeros)
    fun isFilled(): Boolean {
        return cells.all { row -> row.all { cell -> cell.value != 0 } }
    }

    // You might add a function to get a deep copy for certain operations if needed
    fun deepCopy(): SudokuBoard {
        return SudokuBoard(cells.map { row -> row.map { it.copy() } })
    }

    override fun toString(): String {
        return cells.joinToString("\n") { row ->
            row.joinToString(" ") { cell ->
                if (cell.value == 0) "." else cell.value.toString()
            }
        }
    }
}

data class SudokuCell(var value: Int, val isEditable: Boolean)