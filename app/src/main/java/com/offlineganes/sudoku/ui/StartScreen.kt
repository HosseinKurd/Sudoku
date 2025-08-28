package com.offlineganes.sudoku.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.offlineganes.sudoku.R
import com.offlineganes.sudoku.model.Difficulty
import com.offlineganes.sudoku.ui.theme.SudokuTheme

@Composable
fun StartScreen(onStartGame: (Difficulty) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.sudoku_title),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        DifficultySelectionButtons { difficulty ->
            onStartGame(difficulty)
        }
    }
}

@Composable
fun DifficultySelectionButtons(onDifficultySelected: (Difficulty) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.choose_mode_label),
            style = MaterialTheme.typography.titleLarge, // A suitable text style
            modifier = Modifier.padding(bottom = 8.dp) // Add some padding below the label
        )
        Button(
            onClick = { onDifficultySelected(Difficulty.EASY) },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(stringResource(R.string.easy_difficulty))
        }
        Button(
            onClick = { onDifficultySelected(Difficulty.MEDIUM) },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(stringResource(R.string.medium_difficulty))
        }
        Button(
            onClick = { onDifficultySelected(Difficulty.HARD) },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(stringResource(R.string.hard_difficulty))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStartScreen() {
    SudokuTheme {
        StartScreen(onStartGame = {})
    }
}