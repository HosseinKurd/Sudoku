package com.offlineganes.sudoku


import android.annotation.SuppressLint
import com.offlineganes.sudoku.ui.viewmodel.SudokuViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.offlineganes.sudoku.model.Difficulty
import com.offlineganes.sudoku.model.SudokuBoard
import com.offlineganes.sudoku.model.SudokuCell
import com.offlineganes.sudoku.ui.StartScreen
import com.offlineganes.sudoku.ui.theme.SudokuTheme
import kotlinx.coroutines.delay


// Define your navigation routes
object Destinations {
    const val START_SCREEN = "start_screen"
    const val SUDOKU_GAME_SCREEN = "sudoku_game_screen"
    const val SUDOKU_GAME_SCREEN_WITH_DIFFICULTY = "sudoku_game_screen/{difficulty}"
}

class MainActivity : ComponentActivity() {
    // ViewModel is scoped to the activity, so it persists across screen changes
    private val sudokuViewModel: SudokuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SudokuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Destinations.START_SCREEN
                    ) {
                        composable(Destinations.START_SCREEN) {
                            StartScreen(
                                onStartGame = { difficulty ->
                                    navController.navigate("${Destinations.SUDOKU_GAME_SCREEN}/${difficulty.name}")
                                }
                            )
                        }
                        composable(
                            route = Destinations.SUDOKU_GAME_SCREEN_WITH_DIFFICULTY,
                            arguments = listOf(navArgument("difficulty") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val difficultyName = backStackEntry.arguments?.getString("difficulty")
                            val difficulty = difficultyName?.let { Difficulty.valueOf(it) } ?: Difficulty.MEDIUM // Default

                            // Initialize new game with selected difficulty
                            LaunchedEffect(difficulty) {
                                sudokuViewModel.newGame(difficulty)
                            }

                            SudokuScreen(sudokuViewModel) // SudokuScreen now doesn't need difficulty directly
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(viewModel: SudokuViewModel) {
    val sudokuBoard by viewModel.sudokuBoard.collectAsState()
    val selectedCell by viewModel.selectedCell.collectAsState()
    val isGameSolved by viewModel.isGameSolved.collectAsState()
    val showConfetti by viewModel.showConfetti.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Sudoku") })
        },
        bottomBar = {
            NumberInputPad(onNumberClick = { number -> viewModel.onNumberInput(number) }) {
                viewModel.clearSelectedCell()
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SudokuBoardView(
                board = sudokuBoard,
                selectedCell = selectedCell,
                onCellClick = { row, col -> viewModel.selectCell(row, col) }
            )

            // Removed difficulty selection buttons from here
            // They are now on the StartScreen

            if (isGameSolved) {
                Text(
                    text = stringResource(R.string.game_solved_message),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Confetti effect (simple visual indicator)
            AnimatedVisibility(
                visible = showConfetti,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ConfettiEffect { viewModel.confettiShown() }
            }
        }
    }
}

// ... (SudokuBoardView, SudokuCellView, NumberInputPad, ConfettiEffect remain the same as before) ...

@Composable
fun SudokuBoardView(
    board: SudokuBoard,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (row: Int, col: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .aspectRatio(1f) // Make it square
            .border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
            .padding(2.dp)
    ) {
        for (row in 0 until 9) {
            Row(
                modifier = Modifier.weight(1f) // Each row takes equal vertical space
            ) {
                for (col in 0 until 9) {
                    val cell = board.getCell(row, col)
                    val isSelected = selectedCell?.let { it.first == row && it.second == col } ?: false

                    SudokuCellView(
                        cell = cell,
                        isSelected = isSelected,
                        isBoldBorderRight = (col + 1) % 3 == 0 && col != 8,
                        isBoldBorderBottom = (row + 1) % 3 == 0 && row != 8,
                        onClick = { onCellClick(row, col) },
                        modifier = Modifier.weight(1f) // Each cell takes equal horizontal space within the row
                    )
                }
            }
        }
    }
}

@Composable
fun SudokuCellView(
    cell: SudokuCell,
    isSelected: Boolean,
    isBoldBorderRight: Boolean,
    isBoldBorderBottom: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // Add this parameter with a default value
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        !cell.isEditable -> MaterialTheme.colorScheme.surfaceVariant
        else -> Color.Transparent
    }

//    val textColor = if (cell.value == 0) Color.Black else if (!cell.isEditable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary // Example: make user input primary color
    // Corrected textColor logic to distinguish original from user-entered
    val finalTextColor = if (!cell.isEditable) {
        MaterialTheme.colorScheme.onSurface // Original numbers
    } else {
        if (cell.value == 0) MaterialTheme.colorScheme.onSurface // Empty editable
        else MaterialTheme.colorScheme.primary // User-entered editable
    }


    Box(
        modifier = modifier // Apply the incoming modifier here
            .fillMaxHeight() // Keep this to ensure it fills the height of its parent Row
            .background(backgroundColor)
            .run {
                if (isBoldBorderRight) {
                    border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(0.dp))
                } else this
            }
            .run {
                if (isBoldBorderBottom) {
                    border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(0.dp))
                } else this
            }
            .border(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)) // Thin grid lines
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (cell.value == 0) "" else cell.value.toString(),
            fontSize = 24.sp,
            fontWeight = if (!cell.isEditable) FontWeight.Bold else FontWeight.Normal,
            color = finalTextColor // Use the improved text color
        )
    }
}


@Composable
fun NumberInputPad(onNumberClick: (Int) -> Unit, onClearClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Numbers 1-9
        for (row in 0 until 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (col in 0 until 3) {
                    val number = row * 3 + col + 1
                    Button(
                        onClick = { onNumberClick(number) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Text(number.toString(), fontSize = 20.sp)
                    }
                }
            }
        }
        // Clear button
        Button(
            onClick = onClearClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.clear_button), fontSize = 20.sp)
        }
    }
}

@Composable
fun ConfettiEffect(onAnimationEnd: () -> Unit) {
    // A simplified confetti. In a real app, you'd use a dedicated library or more complex animation.
    LaunchedEffect(Unit) {
        delay(2000) // Show for 2 seconds
        onAnimationEnd()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)), // Dim background slightly
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🎉",
            fontSize = 100.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewSudokuScreen() {
    SudokuTheme {
        // Create a dummy ViewModel for preview purposes
        val previewViewModel = SudokuViewModel()
        SudokuScreen(previewViewModel)
    }
}