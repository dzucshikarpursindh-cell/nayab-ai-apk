package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AjrakBlue
import com.example.ui.theme.AjrakMaroon
import com.example.ui.theme.SindhiTeal
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Startup Animated Screen (3 seconds)
 * The Cyborg Robot ("Guda") and Sindh's Historic Landmarks fly in from screen edges
 * and assemble/fix perfectly into the unified NaYaB-Ai banner with cyber-neon effects.
 */
@Composable
fun SplashScreen(
    currentLanguage: String = "sd",
    onFinishSplash: () -> Unit
) {
    // Flying animation progress drivers
    val placesProgress = remember { Animatable(0f) }
    val robotProgress = remember { Animatable(0f) }
    val assembleFlash = remember { Animatable(0f) }
    val titleProgress = remember { Animatable(0f) }
    val timerProgress = remember { Animatable(0f) }

    var isAssembled by remember { mutableStateOf(false) }

    // Pulsing heart animation for the cyborg robot
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_pulse")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart_pulse"
    )
    val neonGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neon_glow"
    )

    // 3-second orchestrator
    LaunchedEffect(Unit) {
        // Step 1 (0 to 600ms): Title flies down
        launch {
            titleProgress.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }

        // Step 2 (300 to 1800ms): Places fly in from corners & robot zooms in from depth
        launch {
            delay(300)
            placesProgress.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
            )
        }
        launch {
            delay(500)
            robotProgress.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
            )
        }

        // Step 3 (1900 to 2400ms): All elements lock & fix into the master artwork with a flash
        launch {
            delay(1900)
            isAssembled = true
            assembleFlash.animateTo(1f, animationSpec = tween(300))
            assembleFlash.animateTo(0f, animationSpec = tween(300))
        }

        // Timer progress 0 -> 1 over 3000ms
        launch {
            timerProgress.animateTo(1f, animationSpec = tween(durationMillis = 3000, easing = LinearEasing))
        }

        // Finish splash after exactly 3.1 seconds
        delay(3100)
        onFinishSplash()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030712),
                        Color(0xFF0A1128),
                        Color(0xFF001F3F),
                        Color(0xFF030712)
                    )
                )
            )
            .testTag("splash_screen_root")
    ) {
        // Skip Button in top corner
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.12f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 20.dp)
                .clickable { onFinishSplash() }
                .testTag("splash_skip_button")
        ) {
            Text(
                text = when (currentLanguage) {
                    "sd" -> "ڇڏيو ➔"
                    "ur" -> "اسکپ ➔"
                    else -> "Skip ➔"
                },
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = maxWidth
            val screenHeight = maxHeight

            // Title flies down from top
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 70.dp)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = (-(100.dp.toPx()) * (1f - titleProgress.value)).roundToInt()
                        )
                    }
                    .alpha(titleProgress.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = SindhiTeal,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NaYaB-Ai",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = Color.White
                    )
                }
                Text(
                    text = "Your Sindhi AI Assistant • سنڌي سمارٽ اسسٽنٽ",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = SindhiTeal.copy(alpha = 0.9f)
                )
            }

            // Main Stage: The assembled banner or flying components
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isAssembled) {
                    // Fully Assembled & Fixed Banner
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .shadow(24.dp, RoundedCornerShape(20.dp), spotColor = SindhiTeal)
                            .border(
                                width = 2.dp,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        SindhiTeal,
                                        WarmAmber,
                                        Color(0xFFFF2A85),
                                        SindhiTeal
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = R.drawable.banner_nayab),
                                contentDescription = "Assembled Banner",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Lock-in Flash effect
                            if (assembleFlash.value > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White.copy(alpha = assembleFlash.value * 0.7f))
                                )
                            }
                        }
                    }
                } else {
                    // Components flying in from all angles towards the center
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val p = placesProgress.value
                        val r = robotProgress.value

                        // Landmark 1: Mohenjo-Daro (Flying from bottom-left)
                        FlyingLandmarkBadge(
                            label = "🏛️ Mohenjo-Daro",
                            sub = "موهن جو دڙو",
                            color = WarmAmber,
                            offsetX = (-260f * (1f - p) - 80f * p).dp,
                            offsetY = (220f * (1f - p) + 70f * p).dp,
                            scale = 0.5f + (0.5f * p),
                            alpha = p
                        )

                        // Landmark 2: Masoom Shah Minaret (Flying from far-left)
                        FlyingLandmarkBadge(
                            label = "🕌 Masoom Shah Minaret",
                            sub = "معصوم شاھ منارو",
                            color = SindhiTeal,
                            offsetX = (-300f * (1f - p) - 100f * p).dp,
                            offsetY = (-120f * (1f - p) - 40f * p).dp,
                            scale = 0.5f + (0.5f * p),
                            alpha = p
                        )

                        // Landmark 3: Rohri Bridge (Flying from top-left)
                        FlyingLandmarkBadge(
                            label = "🌉 Rohri Bridge",
                            sub = "روهڙي پل",
                            color = Color(0xFF00E5FF),
                            offsetX = (-180f * (1f - p) - 20f * p).dp,
                            offsetY = (-240f * (1f - p) - 90f * p).dp,
                            scale = 0.5f + (0.5f * p),
                            alpha = p
                        )

                        // Landmark 4: Quaid-e-Azam Mazar (Flying from bottom-center)
                        FlyingLandmarkBadge(
                            label = "🏛️ Quaid-e-Azam Mazar",
                            sub = "مزار قائد",
                            color = Color.White,
                            offsetX = (0f).dp,
                            offsetY = (260f * (1f - p) + 90f * p).dp,
                            scale = 0.5f + (0.5f * p),
                            alpha = p
                        )

                        // Landmark 5: Faiz Mahal (Flying from bottom-right)
                        FlyingLandmarkBadge(
                            label = "🏰 Faiz Mahal",
                            sub = "فيض محل خيرپور",
                            color = Color(0xFFFF2A85),
                            offsetX = (260f * (1f - p) + 90f * p).dp,
                            offsetY = (200f * (1f - p) + 60f * p).dp,
                            scale = 0.5f + (0.5f * p),
                            alpha = p
                        )

                        // Landmark 6: Garhi Khuda Bux (Flying from top-right)
                        FlyingLandmarkBadge(
                            label = "🏛️ Garhi Khuda Bux",
                            sub = "ڳڙهي خدابخش مزار",
                            color = Color(0xFFE040FB),
                            offsetX = (280f * (1f - p) + 90f * p).dp,
                            offsetY = (-200f * (1f - p) - 60f * p).dp,
                            scale = 0.5f + (0.5f * p),
                            alpha = p
                        )

                        // Center: Cyborg Robot ("Guda") flying in from depth
                        Box(
                            modifier = Modifier
                                .scale(r * 1.1f)
                                .alpha(r)
                                .offset(y = ((1f - r) * 150f).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Robot Hologram Avatar & Pulsing Heart
                                Box(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(
                                                    SindhiTeal.copy(alpha = 0.4f),
                                                    Color(0xFF030712)
                                                )
                                            )
                                        )
                                        .border(2.dp, SindhiTeal, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RocketLaunch,
                                        contentDescription = "Cyborg Robot",
                                        tint = SindhiTeal,
                                        modifier = Modifier.size(54.dp)
                                    )

                                    // Pulsing Neon Heart on Chest
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 12.dp)
                                            .scale(heartScale)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Neon Heart",
                                            tint = Color(0xFFFF1744),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Speech Bubble: "How can I help?"
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF6200EA),
                                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(12.dp))
                                ) {
                                    Text(
                                        text = "⚡ How can I help? • مان ڪيئن مدد ڪريان؟",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Progress & Status Bar (3 seconds timing)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when {
                        timerProgress.value < 0.6f -> when (currentLanguage) {
                            "sd" -> "سڀ تاريخي جايون ۽ سمارٽ اسسٽنٽ جڙي رهيا آهن..."
                            "ur" -> "تمام تاریخی مقامات اور سمارٹ اسسٹنٹ اڑ کر یکجا ہو رہے ہیں..."
                            else -> "Assembling Sindh landmarks & AI cyborg assistant..."
                        }
                        else -> when (currentLanguage) {
                            "sd" -> "ڀلي ڪري آيا! ناياب-اي آئي تيار آهي 🚀"
                            "ur" -> "خوش آمدید! نایاب اے آئی تیار ہے 🚀"
                            else -> "Welcome! NaYaB-Ai is ready 🚀"
                        }
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { timerProgress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = SindhiTeal,
                    trackColor = Color.White.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${((3f - timerProgress.value * 3f) * 10).roundToInt() / 10f}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * Floating badge for each Sindh landmark with neon glow
 */
@Composable
private fun FlyingLandmarkBadge(
    label: String,
    sub: String,
    color: Color,
    offsetX: androidx.compose.ui.unit.Dp,
    offsetY: androidx.compose.ui.unit.Dp,
    scale: Float,
    alpha: Float
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F172A).copy(alpha = 0.88f),
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .scale(scale)
            .alpha(alpha)
            .border(1.5.dp, color, RoundedCornerShape(10.dp))
            .shadow(12.dp, spotColor = color)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = color,
                fontSize = 10.sp
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 8.sp
            )
        }
    }
}
