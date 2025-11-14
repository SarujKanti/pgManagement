package com.skd.pgmanagement.services
import com.skd.pgmanagement.R
object AlphabetImageMapper {
    fun getImageResourceForLetter(firstChar: Char): Int {
        return when (firstChar.lowercaseChar()) {
            'a', 'A' -> R.drawable.bg_image_a
            'b', 'B' -> R.drawable.bg_image_b
            'c', 'C' -> R.drawable.bg_image_a
            'd', 'D' -> R.drawable.bg_image_a
            'e', 'E' -> R.drawable.bg_image_a
            'f', 'F' -> R.drawable.bg_image_a
            'g', 'G' -> R.drawable.bg_image_a
            'h', 'H' -> R.drawable.bg_image_a
            'i', 'I' -> R.drawable.bg_image_a
            'j', 'J' -> R.drawable.bg_image_a
            'k', 'K' -> R.drawable.bg_image_a
            'l', 'L' -> R.drawable.bg_image_a
            'm', 'M' -> R.drawable.bg_image_a
            'n', 'N' -> R.drawable.bg_image_a
            'o', 'O' -> R.drawable.bg_image_a
            'p', 'P' -> R.drawable.bg_image_a
            'q', 'Q' -> R.drawable.bg_image_a
            'r', 'R' -> R.drawable.bg_image_a
            's', 'S' -> R.drawable.bg_image_a
            't', 'T' -> R.drawable.bg_image_a
            'u', 'U' -> R.drawable.bg_image_a
            'v', 'V' -> R.drawable.bg_image_a
            'w', 'W' -> R.drawable.bg_image_a
            'x', 'X' -> R.drawable.bg_image_a
            'y', 'Y' -> R.drawable.bg_image_a
            'z', 'Z' -> R.drawable.bg_image_a
            '1' -> R.drawable.bg_image_a
            '2' -> R.drawable.bg_image_a
            '3' -> R.drawable.bg_image_a
            '4' -> R.drawable.bg_image_a
            '5' -> R.drawable.bg_image_a
            '6' -> R.drawable.bg_image_a
            '7' -> R.drawable.bg_image_a
            '8' -> R.drawable.bg_image_a
            '9' -> R.drawable.bg_image_a
            else -> R.drawable.ic_launcher_background // Default placeholder
        }
    }
}