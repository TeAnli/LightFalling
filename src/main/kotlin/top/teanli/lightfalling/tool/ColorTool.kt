package top.teanli.lightfalling.tool

import java.awt.Color

/**
 * Utility object for color calculations
 */
object ColorTool {
    
    /**
     * Gets color based on percentage (0-100)
     * Red -> Orange -> Yellow -> Light Green -> Bright Green
     */
    fun getPercentageColor(percentage: Int): Color {
        return when {
            percentage < 25 -> Color(255, 100, 100)  // Red
            percentage < 50 -> Color(255, 200, 0)    // Orange
            percentage < 75 -> Color(255, 255, 0)    // Yellow
            percentage < 100 -> Color(150, 255, 100) // Light green
            else -> Color(0, 255, 0)                 // Bright green
        }
    }

    /**
     * Gets color based on time remaining (in seconds)
     * Green -> Yellow -> Orange -> Red
     */
    fun getTimeColor(timeLeft: Float): Color {
        return when {
            timeLeft > 2.0f -> Color(0, 255, 0)      // Green
            timeLeft > 1.0f -> Color(255, 255, 0)    // Yellow
            timeLeft > 0.5f -> Color(255, 165, 0)    // Orange
            else -> Color(255, 0, 0)                 // Red
        }
    }

    /**
     * Gets color based on signal strength (0-15)
     * Gray -> Light Red -> Orange -> Yellow -> Light Green -> Bright Green
     */
    fun getSignalColor(signal: Int): Color {
        return when {
            signal == 0 -> Color.GRAY
            signal <= 3 -> Color(255, 100, 100)      // Light red
            signal <= 7 -> Color(255, 200, 0)        // Orange
            signal <= 11 -> Color(255, 255, 0)       // Yellow
            signal <= 14 -> Color(100, 255, 100)     // Light green
            else -> Color(0, 255, 0)                 // Bright green
        }
    }

    /**
     * Interpolates between two colors based on a factor (0.0 to 1.0)
     */
    fun lerp(color1: Color, color2: Color, factor: Float): Color {
        val clampedFactor = factor.coerceIn(0f, 1f)
        val r = (color1.red + (color2.red - color1.red) * clampedFactor).toInt()
        val g = (color1.green + (color2.green - color1.green) * clampedFactor).toInt()
        val b = (color1.blue + (color2.blue - color1.blue) * clampedFactor).toInt()
        return Color(r, g, b)
    }
}
