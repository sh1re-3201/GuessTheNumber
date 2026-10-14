package com.example.guessthenumber

import kotlin.math.abs
import kotlin.random.Random

/**
 * Proximity tier representing how close the user's guess is to the secret target.
 */
enum class FeedbackTier(val title: String, val description: String) {
    INITIAL(
        title = "Ready to Play?",
        description = "Enter any number between 0 and 100 to start!"
    ),
    CORRECT_GOLD(
        title = "Jackpot! Correct!",
        description = "You guessed the exact number! Enjoy the fireworks!"
    ),
    WITHIN_5_BLUE_STARS(
        title = "Burning Hot! (Within 5)",
        description = "You're extremely close! The starry sky is shining bright."
    ),
    WITHIN_20_NAVY_CLOUDS(
        title = "Getting Warm! (Within 20)",
        description = "You're within 20 numbers. Keep going through the clouds!"
    ),
    WITHIN_50_BLACK_SOMBER(
        title = "Cold... (Within 50)",
        description = "You're within 50 numbers. The atmosphere has turned somber."
    ),
    FURTHEST_BLACK_RED_TEXT(
        title = "Furthest Away!",
        description = "You've chosen the furthest away answer possible!"
    )
}

/**
 * Direction hint relative to target.
 */
enum class DirectionHint(val text: String) {
    CORRECT("Exact Match!"),
    TOO_HIGH("Try a lower number"),
    TOO_LOW("Try a higher number")
}

/**
 * Record of a single guess attempt.
 */
data class GuessAttempt(
    val guess: Int,
    val diff: Int,
    val tier: FeedbackTier,
    val direction: DirectionHint
)

/**
 * Overall game state.
 */
data class GameState(
    val targetNumber: Int = generateRandomTarget(),
    val currentInput: String = "",
    val attempts: List<GuessAttempt> = emptyList(),
    val currentTier: FeedbackTier = FeedbackTier.INITIAL,
    val lastGuess: Int? = null,
    val isGameOver: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Generates an integer between 0 and 100 inclusive.
 */
fun generateRandomTarget(): Int {
    val target = Random.nextInt(0, 101)
    println("🎯 [GuessTheNumber] Generated secret target number: $target")
    try {
        android.util.Log.d("GuessTheNumber", "Generated secret target number: $target")
    } catch (_: Throwable) {
        // Safe fallback for local JVM tests where android.util.Log is not mocked
    }
    return target
}

/**
 * Evaluates the proximity tier based on absolute difference.
 * - diff == 0: Exact match (Gold + Fireworks)
 * - 1 <= diff <= 5: Within 5 (Blue + Twinkling Stars)
 * - 6 <= diff <= 20: Within 20 (Navy + Clouds)
 * - 21 <= diff <= 50: Within 50 (Black + Somber Palette)
 * - 51 <= diff <= 100: Furthest tier (Black + Red Text)
 */
fun evaluateGuess(guess: Int, target: Int): FeedbackTier {
    val diff = abs(guess - target)
    return when {
        diff == 0 -> FeedbackTier.CORRECT_GOLD
        diff in 1..5 -> FeedbackTier.WITHIN_5_BLUE_STARS
        diff in 6..20 -> FeedbackTier.WITHIN_20_NAVY_CLOUDS
        diff in 21..50 -> FeedbackTier.WITHIN_50_BLACK_SOMBER
        else -> FeedbackTier.FURTHEST_BLACK_RED_TEXT
    }
}

/**
 * Determines whether the guess is higher, lower, or correct.
 */
fun evaluateDirection(guess: Int, target: Int): DirectionHint {
    return when {
        guess == target -> DirectionHint.CORRECT
        guess > target -> DirectionHint.TOO_HIGH
        else -> DirectionHint.TOO_LOW
    }
}
