package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val SourceSerif4FontFamily = FontFamily(
    Font(R.font.source_serif_4, FontWeight.Normal),
    Font(R.font.source_serif_4, FontWeight.Medium),
    Font(R.font.source_serif_4, FontWeight.SemiBold),
    Font(R.font.source_serif_4, FontWeight.Bold)
)

val PlusJakartaSansFontFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold)
)

fun buildAppTypography(scale: Float = 1.0f): Typography {
    val s = scale.coerceIn(0.9f, 1.4f)
    return Typography(
        displaySmall = TextStyle(
            fontFamily = SourceSerif4FontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (30f * s).sp,
            lineHeight = (38f * s).sp,
            letterSpacing = (-0.25).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = SourceSerif4FontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (26f * s).sp,
            lineHeight = (34f * s).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = SourceSerif4FontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (22f * s).sp,
            lineHeight = (30f * s).sp
        ),
        headlineSmall = TextStyle(
            fontFamily = SourceSerif4FontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (19f * s).sp,
            lineHeight = (26f * s).sp
        ),
        titleLarge = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (20f * s).sp,
            lineHeight = (28f * s).sp
        ),
        titleMedium = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (17f * s).sp,
            lineHeight = (24f * s).sp,
            letterSpacing = 0.1.sp
        ),
        titleSmall = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (15f * s).sp,
            lineHeight = (22f * s).sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (17f * s).sp,
            lineHeight = (26f * s).sp,
            letterSpacing = 0.15.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (15f * s).sp,
            lineHeight = (23f * s).sp,
            letterSpacing = 0.15.sp
        ),
        bodySmall = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (13.5f * s).sp,
            lineHeight = (20f * s).sp,
            letterSpacing = 0.2.sp
        ),
        labelLarge = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (15f * s).sp,
            lineHeight = (20f * s).sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = (13f * s).sp,
            lineHeight = (18f * s).sp,
            letterSpacing = 0.2.sp
        ),
        labelSmall = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = (12f * s).sp,
            lineHeight = (16f * s).sp,
            letterSpacing = 0.25.sp
        )
    )
}
