package com.example.guessthenumber

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guessthenumber.ui.theme.GuessTheNumberTheme

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    var gameState by remember { mutableStateOf(GameState()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val submitGuess = {
        val input = gameState.currentInput.trim()
        val guess = input.toIntOrNull()

        when {
            guess == null -> {
                gameState = gameState.copy(errorMessage = "Please enter a valid number (0–100)")
            }
            guess !in 0..100 -> {
                gameState = gameState.copy(errorMessage = "Number must be between 0 and 100")
            }
            else -> {
                val isCorrect = (guess == gameState.targetNumber)
                val nextScore = calculateNextScore(gameState.score, isCorrect)
                val isGameOver = isCorrect || nextScore == 0
                val isGameWon = isCorrect

                val diff = kotlin.math.abs(guess - gameState.targetNumber)
                val tier = evaluateGuess(guess, gameState.targetNumber)
                val direction = evaluateDirection(guess, gameState.targetNumber)
                val newAttempt = GuessAttempt(
                    guess = guess,
                    diff = diff,
                    tier = tier,
                    direction = direction
                )

                gameState = gameState.copy(
                    attempts = listOf(newAttempt) + gameState.attempts,
                    currentTier = tier,
                    lastGuess = guess,
                    score = nextScore,
                    isGameOver = isGameOver,
                    isGameWon = isGameWon,
                    currentInput = "",
                    errorMessage = null
                )
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        }
    }

    val resetGame = {
        gameState = GameState()
        focusManager.clearFocus()
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Dynamic animated background for the current tier
        DynamicGameBackground(
            tier = gameState.currentTier,
            modifier = Modifier.fillMaxSize()
        )

        // Game UI Layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header / App Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp), // Adds padding to the right (end) side
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = resetGame,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "↺ Reset",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Guess The Number",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Pick a secret number between 0 and 100",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFD700).copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.7f))
                    ) {
                        Text(
                            text = "⭐ ${gameState.score}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFD700)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Interactive Glass Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.65f)
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Status Badge / Feedback
                    val statusTitle = when {
                        gameState.isGameWon -> "Jackpot! You Won!"
                        gameState.isGameOver && !gameState.isGameWon -> "Game Over!"
                        else -> gameState.currentTier.title
                    }

                    val statusDescription = when {
                        gameState.isGameWon -> "Final Score: ${gameState.score} / 100! Enjoy the fireworks!"
                        gameState.isGameOver && !gameState.isGameWon -> "You ran out of points (-10 per wrong guess). The secret number was ${gameState.targetNumber}."
                        else -> gameState.currentTier.description
                    }

                    val titleColor = when {
                        gameState.isGameWon -> Color(0xFFFFD700)
                        gameState.isGameOver && !gameState.isGameWon -> Color(0xFFFF1744)
                        else -> getTierAccentColor(gameState.currentTier)
                    }

                    Text(
                        text = statusTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = titleColor
                        ),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = statusDescription,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        ),
                        textAlign = TextAlign.Center
                    )

                    // Direction Hint (Too high / Too low)
                    if (gameState.lastGuess != null && !gameState.isGameOver) {
                        val lastAttempt = gameState.attempts.firstOrNull()
                        if (lastAttempt != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.12f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Last Guess: ${lastAttempt.guess} → ${lastAttempt.direction.text}",
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    // Input Field (disabled when game is won or out of points)
                    OutlinedTextField(
                        value = gameState.currentInput,
                        onValueChange = { newValue ->
                            // Allow digits only and at most 3 characters
                            if (newValue.length <= 3 && newValue.all { it.isDigit() }) {
                                gameState = gameState.copy(
                                    currentInput = newValue,
                                    errorMessage = null
                                )
                            }
                        },
                        enabled = !gameState.isGameOver,
                        label = { Text("Your Guess (0 - 100)") },
                        placeholder = { Text("e.g. 50") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { submitGuess() }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = getTierAccentColor(gameState.currentTier),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedLabelColor = getTierAccentColor(gameState.currentTier),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Validation Error Text
                    AnimatedVisibility(
                        visible = gameState.errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = gameState.errorMessage.orEmpty(),
                            color = Color(0xFFFF5252),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Action Buttons
                    if (gameState.isGameOver) {
                        Button(
                            onClick = resetGame,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (gameState.isGameWon) Color(0xFFFFD700) else Color(0xFFFF5252),
                                contentColor = if (gameState.isGameWon) Color.Black else Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = if (gameState.isGameWon) "🎉 Play Again!" else "↺ Try Again (Reset to 100)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    } else {
                        Button(
                            onClick = submitGuess,
                            enabled = gameState.currentInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getTierAccentColor(gameState.currentTier),
                                contentColor = if (gameState.currentTier == FeedbackTier.CORRECT_GOLD) Color.Black else Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Submit Guess →",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // Attempts Summary & History Chips
            if (gameState.attempts.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Guess History",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Total: ${gameState.attempts.size} attempt(s)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            )
                        }

                        // Horizontal list of chips for prior guesses
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            gameState.attempts.forEach { attempt ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = getTierAccentColor(attempt.tier).copy(alpha = 0.25f),
                                    border = BorderStroke(1.dp, getTierAccentColor(attempt.tier).copy(alpha = 0.6f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${attempt.guess}",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = if (attempt.diff == 0) "Match" else "",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getTierAccentColor(tier: FeedbackTier): Color {
    return when (tier) {
        FeedbackTier.INITIAL -> Color(0xFF64B5F6)
        FeedbackTier.CORRECT_GOLD -> Color(0xFFFFD700)
        FeedbackTier.WITHIN_5_BLUE_STARS -> Color(0xFF42A5F5)
        FeedbackTier.WITHIN_20_NAVY_CLOUDS -> Color(0xFF90CAF9)
        FeedbackTier.WITHIN_50_BLACK_SOMBER -> Color(0xFFB0BEC5)
        FeedbackTier.FURTHEST_BLACK_RED_TEXT -> Color(0xFFFF1744)
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GuessTheNumberTheme {
        GameScreen()
    }
}
