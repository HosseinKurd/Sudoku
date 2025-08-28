package com.offlineganes.sudoku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlineganes.sudoku.model.Difficulty
import com.offlineganes.sudoku.model.SudokuBoard
import com.offlineganes.sudoku.model.SudokuGenerator
import com.offlineganes.sudoku.model.SudokuValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SudokuViewModel : ViewModel() {

    private val _sudokuBoard = MutableStateFlow(SudokuGenerator.generateSudokuBoard()) // Initial board
    val sudokuBoard: StateFlow<SudokuBoard> = _sudokuBoard.asStateFlow()

    private val _selectedCell = MutableStateFlow<Pair<Int, Int>?>(null)
    val selectedCell: StateFlow<Pair<Int, Int>?> = _selectedCell.asStateFlow()

    private val _isGameSolved = MutableStateFlow(false)
    val isGameSolved: StateFlow<Boolean> = _isGameSolved.asStateFlow()

    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    init {
        // Initial check for game solved state
        checkGameSolved()
    }

    fun newGame(difficulty: Difficulty) {
        viewModelScope.launch {
            _sudokuBoard.value = SudokuGenerator.generateSudokuBoard(difficulty)
            _selectedCell.value = null // Deselect any cell
            _isGameSolved.value = false
            _showConfetti.value = false
        }
    }

    fun selectCell(row: Int, col: Int) {
        _selectedCell.value = Pair(row, col)
    }

    fun deselectCell() {
        _selectedCell.value = null
    }

    fun onNumberInput(number: Int) {
        val (row, col) = _selectedCell.value ?: return // No cell selected
        val currentBoard = _sudokuBoard.value
        val cellToUpdate = currentBoard.getCell(row, col)

        if (cellToUpdate.isEditable) {
            val newBoard = currentBoard.updateCell(row, col, number)
            _sudokuBoard.value = newBoard
            checkGameSolved()
        }
    }

    fun clearSelectedCell() {
        val (row, col) = _selectedCell.value ?: return
        val currentBoard = _sudokuBoard.value
        val cellToClear = currentBoard.getCell(row, col)

        if (cellToClear.isEditable) {
            val newBoard = currentBoard.updateCell(row, col, 0) // Set to 0 (empty)
            _sudokuBoard.value = newBoard
            _isGameSolved.value = false // Game is no longer solved if we clear a cell
        }
    }

    private fun checkGameSolved() {
        viewModelScope.launch {
            val currentBoard = _sudokuBoard.value
            val solved = SudokuValidator.isGameSolved(currentBoard)
            _isGameSolved.value = solved
            if (solved) {
                _showConfetti.value = true
            }
        }
    }

    fun confettiShown() {
        _showConfetti.value = false
    }

    // Optional: Hint functionality (more complex, involves solving a copy)
    fun getHint() {
        viewModelScope.launch {
            val currentBoard = _sudokuBoard.value.deepCopy()
            val solutionBoard = SudokuBoard(Array(9) { IntArray(9) })
            currentBoard.cells.forEachIndexed { r, rowCells ->
                rowCells.forEachIndexed { c, cell ->
                    solutionBoard.cells[r][c].value = cell.value // Initialize with current values
                }
            }

            if (SudokuGenerator.solveBoard(currentBoard.cells.map { row ->
                    row.map { it.value }.toIntArray()
                }.toTypedArray())) {
                // Find a random empty cell and fill it with the solution
                val emptyCells = mutableListOf<Pair<Int, Int>>()
                _sudokuBoard.value.cells.forEachIndexed { r, rowCells ->
                    rowCells.forEachIndexed { c, cell ->
                        if (cell.value == 0) {
                            emptyCells.add(Pair(r, c))
                        }
                    }
                }

                if (emptyCells.isNotEmpty()) {
                    val (row, col) = emptyCells.random()
                    // Get the solution for that specific cell
                    val solvedValue = solutionBoard.cells[row][col].value
                    onNumberInput(solvedValue) // Update the board with the hint
                }
            }
        }
    }
}