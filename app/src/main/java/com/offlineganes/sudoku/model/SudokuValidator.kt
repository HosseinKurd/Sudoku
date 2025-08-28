package com.offlineganes.sudoku.model

object SudokuValidator {

    // Checks if the current board configuration is valid (no immediate conflicts)
    fun isValidSudoku(board: SudokuBoard): Boolean {
        val cells = board.cells
        // Check rows
        for (row in 0 until 9) {
            if (!isUnique(cells[row].map { it.value })) return false
        }
        // Check columns
        for (col in 0 until 9) {
            if (!isUnique(cells.map { it[col].value })) return false
        }
        // Check 3x3 boxes
        for (rowBox in 0 until 3) {
            for (colBox in 0 until 3) {
                val boxValues = mutableListOf<Int>()
                for (row in rowBox * 3 until rowBox * 3 + 3) {
                    for (col in colBox * 3 until colBox * 3 + 3) {
                        boxValues.add(cells[row][col].value)
                    }
                }
                if (!isUnique(boxValues)) return false
            }
        }
        return true
    }

    // Helper function to check for unique non-zero values in a list
    private fun isUnique(list: List<Int>): Boolean {
        val seen = mutableSetOf<Int>()
        for (value in list) {
            if (value != 0) { // Ignore zeros (empty cells)
                if (seen.contains(value)) {
                    return false
                }
                seen.add(value)
            }
        }
        return true
    }

    // Checks if the entire Sudoku is solved correctly (all cells filled and valid)
    fun isGameSolved(board: SudokuBoard): Boolean {
        return board.isFilled() && isValidSudoku(board)
    }
}