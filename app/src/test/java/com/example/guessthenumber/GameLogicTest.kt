package com.example.guessthenumber

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLogicTest {

    @Test
    fun `diff 0 produces gold fireworks tier`() {
        assertEquals(FeedbackTier.CORRECT_GOLD, evaluateGuess(guess = 42, target = 42))
        assertEquals(FeedbackTier.CORRECT_GOLD, evaluateGuess(guess = 0, target = 0))
        assertEquals(FeedbackTier.CORRECT_GOLD, evaluateGuess(guess = 100, target = 100))
    }

    @Test
    fun `diff 1 to 5 produces blue stars tier`() {
        // Lower boundary of tier 2
        assertEquals(FeedbackTier.WITHIN_5_BLUE_STARS, evaluateGuess(guess = 49, target = 50))
        assertEquals(FeedbackTier.WITHIN_5_BLUE_STARS, evaluateGuess(guess = 51, target = 50))
        // Upper boundary of tier 2
        assertEquals(FeedbackTier.WITHIN_5_BLUE_STARS, evaluateGuess(guess = 45, target = 50))
        assertEquals(FeedbackTier.WITHIN_5_BLUE_STARS, evaluateGuess(guess = 55, target = 50))
    }

    @Test
    fun `diff 6 to 20 produces navy clouds tier`() {
        // Lower boundary of tier 3
        assertEquals(FeedbackTier.WITHIN_20_NAVY_CLOUDS, evaluateGuess(guess = 44, target = 50))
        assertEquals(FeedbackTier.WITHIN_20_NAVY_CLOUDS, evaluateGuess(guess = 56, target = 50))
        // Upper boundary of tier 3
        assertEquals(FeedbackTier.WITHIN_20_NAVY_CLOUDS, evaluateGuess(guess = 30, target = 50))
        assertEquals(FeedbackTier.WITHIN_20_NAVY_CLOUDS, evaluateGuess(guess = 70, target = 50))
    }

    @Test
    fun `diff 21 to 50 produces black somber tier`() {
        // Lower boundary of tier 4
        assertEquals(FeedbackTier.WITHIN_50_BLACK_SOMBER, evaluateGuess(guess = 29, target = 50))
        assertEquals(FeedbackTier.WITHIN_50_BLACK_SOMBER, evaluateGuess(guess = 71, target = 50))
        // Upper boundary of tier 4
        assertEquals(FeedbackTier.WITHIN_50_BLACK_SOMBER, evaluateGuess(guess = 0, target = 50))
        assertEquals(FeedbackTier.WITHIN_50_BLACK_SOMBER, evaluateGuess(guess = 100, target = 50))
    }

    @Test
    fun `diff 51 to 100 produces furthest black red text tier`() {
        // User example: Target is 100 and guess is 1 (diff = 99)
        assertEquals(FeedbackTier.FURTHEST_BLACK_RED_TEXT, evaluateGuess(guess = 1, target = 100))
        // Extreme diff 100 (0 vs 100)
        assertEquals(FeedbackTier.FURTHEST_BLACK_RED_TEXT, evaluateGuess(guess = 0, target = 100))
        assertEquals(FeedbackTier.FURTHEST_BLACK_RED_TEXT, evaluateGuess(guess = 100, target = 0))
        // Lower boundary of tier 5 (diff = 51)
        assertEquals(FeedbackTier.FURTHEST_BLACK_RED_TEXT, evaluateGuess(guess = 51, target = 0))
    }

    @Test
    fun `directional hint provides correct guidance`() {
        assertEquals(DirectionHint.CORRECT, evaluateDirection(guess = 50, target = 50))
        assertEquals(DirectionHint.TOO_HIGH, evaluateDirection(guess = 75, target = 50))
        assertEquals(DirectionHint.TOO_LOW, evaluateDirection(guess = 25, target = 50))
    }

    @Test
    fun `random target generation remains strictly in 0 to 100`() {
        repeat(200) {
            val target = generateRandomTarget()
            assertTrue("Target $target should be >= 0", target >= 0)
            assertTrue("Target $target should be <= 100", target <= 100)
        }
    }
}
