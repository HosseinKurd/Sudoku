package com.offlineganes.sudoku.model

object SudokuGenerator {

    // Generates a new, solvable Sudoku board with a certain number of empty cells
    fun generateSudokuBoard(difficulty: Difficulty = Difficulty.MEDIUM): SudokuBoard {
        val board = Array(9) { IntArray(9) }
        fillDiagonalBoxes(board) // Fill diagonal 3x3 boxes first
        solveBoard(board) // Solve the board completely

        // Remove numbers to create the puzzle
        val cellsToRemove = when (difficulty) {
            Difficulty.EASY -> 40
            Difficulty.MEDIUM -> 50
            Difficulty.HARD -> 60
        }
        removeCells(board, cellsToRemove)

        return SudokuBoard(board)
    }

    // Helper to fill the diagonal 3x3 boxes (optimization for generation)
    private fun fillDiagonalBoxes(board: Array<IntArray>) {
        for (i in 0 until 9 step 3) {
            fillBox(board, i, i)
        }
    }

    private fun fillBox(board: Array<IntArray>, rowStart: Int, colStart: Int) {
        val nums = (1..9).shuffled().toMutableList()
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                board[rowStart + i][colStart + j] = nums.removeAt(0)
            }
        }
    }

    // Backtracking algorithm to solve the Sudoku board
    fun solveBoard(board: Array<IntArray>): Boolean {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (board[row][col] == 0) { // Find empty cell
                    for (num in 1..9) {
                        if (isValidMove(board, row, col, num)) {
                            board[row][col] = num
                            if (solveBoard(board)) { // Recurse
                                return true
                            } else {
                                board[row][col] = 0 // Backtrack
                            }
                        }
                    }
                    return false // No number works for this cell
                }
            }
        }
        return true // Board is solved
    }

    // Checks if placing 'num' at (row, col) is valid
    private fun isValidMove(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        // Check row
        for (x in 0 until 9) {
            if (board[row][x] == num) return false
        }
        // Check column
        for (x in 0 until 9) {
            if (board[x][col] == num) return false
        }
        // Check 3x3 box
        val startRow = row - row % 3
        val startCol = col - col % 3
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (board[startRow + i][startCol + j] == num) return false
            }
        }
        return true
    }

    // Removes cells to create the puzzle
    private fun removeCells(board: Array<IntArray>, count: Int) {
        var removedCount = 0
        while (removedCount < count) {
            val row = (0..8).random()
            val col = (0..8).random()
            if (board[row][col] != 0) {
                val temp = board[row][col]
                board[row][col] = 0 // Try removing
                // You might add a check here to ensure the puzzle still has a unique solution
                // For simplicity, we'll skip the unique solution check for now.
                removedCount++
            }
        }
    }
}

enum class Difficulty {
    EASY, MEDIUM, HARD
}