package com.example.guessthenumber

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// -------------------------------------------------------------
// 1. Correct Answer: Gold Theme + Launching & Bursting Firework
// -------------------------------------------------------------

private data class Spark(
    val angle: Float,
    val speed: Float,
    val color: Color,
    val size: Float
)

@Composable
fun GoldFireworksEffect(modifier: Modifier = Modifier) {
    val sparks = remember {
        val random = Random(42)
        val goldColors = listOf(
            Color(0xFFFFD700),
            Color(0xFFFFA500),
            Color(0xFFFFF8DC),
            Color(0xFFFFE082),
            Color(0xFFFFF176),
            Color(0xFFFFB300)
        )
        List(48) {
            val angle = random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = 100f + random.nextFloat() * 260f
            Spark(
                angle = angle,
                speed = speed,
                color = goldColors[random.nextInt(goldColors.size)],
                size = 3f + random.nextFloat() * 5f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "FireworksTransition")
    val animProgress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FireworksProgress"
    )

    val goldBg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2E2000),
            Color(0xFF7A5800),
            Color(0xFFB8860B),
            Color(0xFFD4AF37)
        )
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(goldBg)
    ) {
        val t = animProgress.value
        val centerX = size.width / 2f
        val centerY = size.height * 0.42f

        if (t < 0.4f) {
            // Rocket launch phase: flies from bottom to center
            val launchT = t / 0.4f
            val rocketY = size.height - (size.height - centerY) * launchT

            // Rocket head
            drawCircle(
                color = Color(0xFFFFF8DC),
                radius = 6.dp.toPx(),
                center = Offset(centerX, rocketY)
            )

            // Rocket spark trail
            for (i in 1..6) {
                val trailY = rocketY + i * 9.dp.toPx()
                val trailAlpha = (1f - i / 7f) * (1f - launchT * 0.3f)
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = trailAlpha),
                    radius = (5 - i * 0.6f).coerceAtLeast(1.5f).dp.toPx(),
                    center = Offset(centerX + sin(i * 1.5f) * 4f, trailY)
                )
            }
        } else {
            // Burst phase: particles explode outward from center
            val burstT = (t - 0.4f) / 0.6f
            val alpha = (1f - burstT).coerceIn(0f, 1f)

            // Initial flash
            if (burstT < 0.15f) {
                val flashAlpha = (1f - burstT / 0.15f) * 0.6f
                drawCircle(
                    color = Color.White.copy(alpha = flashAlpha),
                    radius = (40f + burstT * 120f).dp.toPx(),
                    center = Offset(centerX, centerY)
                )
            }

            // Expanding spark particles
            sparks.forEach { spark ->
                val distance = spark.speed * burstT * density
                val sparkX = centerX + cos(spark.angle) * distance
                val gravityDrop = 80f * burstT * burstT * density
                val sparkY = centerY + sin(spark.angle) * distance + gravityDrop

                drawCircle(
                    color = spark.color.copy(alpha = alpha),
                    radius = (spark.size * (1f - burstT * 0.4f)).dp.toPx(),
                    center = Offset(sparkX, sparkY)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 2. Within 5: Celestial Blue Theme + Twinkling Stars
// -------------------------------------------------------------

private data class Star(
    val normX: Float,
    val normY: Float,
    val radiusDp: Float,
    val phaseOffset: Float,
    val speedFactor: Float
)

@Composable
fun StarrySkyEffect(modifier: Modifier = Modifier) {
    val stars = remember {
        val random = Random(123)
        List(55) {
            Star(
                normX = random.nextFloat(),
                normY = random.nextFloat(),
                radiusDp = 1.8f + random.nextFloat() * 3.2f,
                phaseOffset = random.nextFloat() * (2f * Math.PI.toFloat()),
                speedFactor = 0.8f + random.nextFloat() * 1.4f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "StarTwinkleTransition")
    val time = transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "StarTwinkleTime"
    )

    val skyBlueBg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF061539),
            Color(0xFF0D47A1),
            Color(0xFF1976D2),
            Color(0xFF29B6F6)
        )
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(skyBlueBg)
    ) {
        val currentT = time.value
        stars.forEach { star ->
            val starX = star.normX * size.width
            val starY = star.normY * size.height

            val twinkle = (sin(currentT * star.speedFactor + star.phaseOffset) * 0.45f + 0.55f).coerceIn(0.15f, 1f)

            // Outer soft glow
            drawCircle(
                color = Color(0xFFBBDEFB).copy(alpha = twinkle * 0.4f),
                radius = (star.radiusDp * 1.8f).dp.toPx(),
                center = Offset(starX, starY)
            )

            // Inner bright core
            drawCircle(
                color = Color.White.copy(alpha = twinkle),
                radius = star.radiusDp.dp.toPx(),
                center = Offset(starX, starY)
            )
        }
    }
}

// -------------------------------------------------------------
// 3. Within 20: Navy Theme + Drifting Clouds
// -------------------------------------------------------------

private data class Cloud(
    val initialNormX: Float,
    val normY: Float,
    val scale: Float,
    val speed: Float,
    val alpha: Float
)

@Composable
fun CloudySkyEffect(modifier: Modifier = Modifier) {
    val clouds = remember {
        listOf(
            Cloud(initialNormX = 0.05f, normY = 0.18f, scale = 1.1f, speed = 0.045f, alpha = 0.38f),
            Cloud(initialNormX = 0.55f, normY = 0.32f, scale = 0.9f, speed = 0.060f, alpha = 0.45f),
            Cloud(initialNormX = 0.20f, normY = 0.52f, scale = 1.3f, speed = 0.035f, alpha = 0.32f),
            Cloud(initialNormX = 0.75f, normY = 0.70f, scale = 1.0f, speed = 0.050f, alpha = 0.40f),
            Cloud(initialNormX = -0.15f, normY = 0.85f, scale = 1.2f, speed = 0.040f, alpha = 0.35f)
        )
    }

    val transition = rememberInfiniteTransition(label = "CloudDriftTransition")
    val driftProgress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CloudDrift"
    )

    val navyBg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF070E20),
            Color(0xFF0F2042),
            Color(0xFF162D5A),
            Color(0xFF1E3A8A)
        )
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(navyBg)
    ) {
        clouds.forEach { cloud ->
            // Wrap horizontally around the screen width
            val totalDistance = (cloud.initialNormX + driftProgress.value * cloud.speed * 20f) % 1.4f
            val cloudX = (totalDistance - 0.2f) * size.width
            val cloudY = cloud.normY * size.height

            drawPuffyCloud(
                centerX = cloudX,
                centerY = cloudY,
                scale = cloud.scale,
                alpha = cloud.alpha
            )
        }
    }
}

private fun DrawScope.drawPuffyCloud(centerX: Float, centerY: Float, scale: Float, alpha: Float) {
    val cloudColor = Color(0xFFDDE3F0).copy(alpha = alpha)
    val r = 36.dp.toPx() * scale

    // Compose cloud out of overlapping circular puffs
    drawCircle(color = cloudColor, radius = r * 0.9f, center = Offset(centerX - r * 1.1f, centerY + r * 0.1f))
    drawCircle(color = cloudColor, radius = r * 1.2f, center = Offset(centerX, centerY - r * 0.2f))
    drawCircle(color = cloudColor, radius = r * 0.85f, center = Offset(centerX + r * 1.1f, centerY + r * 0.15f))
    drawCircle(color = cloudColor, radius = r * 0.7f, center = Offset(centerX - r * 0.5f, centerY + r * 0.35f))
    drawCircle(color = cloudColor, radius = r * 0.75f, center = Offset(centerX + r * 0.5f, centerY + r * 0.35f))
}

// -------------------------------------------------------------
// 4. Within 50: Somber Black / Charcoal Theme
// -------------------------------------------------------------

@Composable
fun SomberEffect(modifier: Modifier = Modifier) {
    val somberBg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0A0A),
            Color(0xFF141414),
            Color(0xFF1C1C1C),
            Color(0xFF101010)
        )
    )

    val transition = rememberInfiniteTransition(label = "SomberVignette")
    val pulse = transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SomberPulse"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(somberBg)
    ) {
        // Draw gloomy ambient radial mist in the center
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF2C2C2C).copy(alpha = pulse.value),
                    Color.Transparent
                ),
                center = center,
                radius = size.minDimension * 0.7f
            )
        )
    }
}

// -------------------------------------------------------------
// 5. Furthest Away (Within 99 / > 50): Pitch Black + Glowing Red Text
// -------------------------------------------------------------

@Composable
fun FurthestAwayEffect(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "RedPulseTransition")
    val alphaAnim = transition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RedPulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = "YOU'VE CHOSEN THE FURTHEST AWAY ANSWER POSSIBLE!",
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 80.dp),
            style = TextStyle(
                color = Color(0xFFFF1744).copy(alpha = alphaAnim.value),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                letterSpacing = 1.5.sp,
                shadow = Shadow(
                    color = Color(0xFFFF0000).copy(alpha = 0.8f),
                    blurRadius = 18f
                )
            )
        )
    }
}

// -------------------------------------------------------------
// Dynamic Background Switcher
// -------------------------------------------------------------

@Composable
fun DynamicGameBackground(
    tier: FeedbackTier,
    modifier: Modifier = Modifier
) {
    when (tier) {
        FeedbackTier.INITIAL -> {
            // Default sleek dark slate gradient before any guess
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
                        )
                    )
            )
        }
        FeedbackTier.CORRECT_GOLD -> GoldFireworksEffect(modifier)
        FeedbackTier.WITHIN_5_BLUE_STARS -> StarrySkyEffect(modifier)
        FeedbackTier.WITHIN_20_NAVY_CLOUDS -> CloudySkyEffect(modifier)
        FeedbackTier.WITHIN_50_BLACK_SOMBER -> SomberEffect(modifier)
        FeedbackTier.FURTHEST_BLACK_RED_TEXT -> FurthestAwayEffect(modifier)
    }
}
