package com.skd.pgmanagement.services
import com.skd.pgmanagement.R
object AlphabetImageMapper {
    fun getImageResourceForLetter(firstChar: Char): Int {
        return when (firstChar.lowercaseChar()) {
            'a', 'A' -> R.drawable.bg_image_a
            'b', 'B' -> R.drawable.bg_image_b
            'c', 'C' -> R.drawable.bg_image_c
            'd', 'D' -> R.drawable.bg_image_d
            'e', 'E' -> R.drawable.bg_image_e
            'f', 'F' -> R.drawable.bg_image_f
            'g', 'G' -> R.drawable.bg_image_g
            'h', 'H' -> R.drawable.bg_image_h
            'i', 'I' -> R.drawable.bg_image_i
            'j', 'J' -> R.drawable.bg_image_j
            'k', 'K' -> R.drawable.bg_image_k
            'l', 'L' -> R.drawable.bg_image_l
            'm', 'M' -> R.drawable.bg_image_m
            'n', 'N' -> R.drawable.bg_image_n
            'o', 'O' -> R.drawable.bg_image_o
            'p', 'P' -> R.drawable.bg_image_p
            'q', 'Q' -> R.drawable.bg_image_q
            'r', 'R' -> R.drawable.bg_image_r
            's', 'S' -> R.drawable.bg_image_s
            't', 'T' -> R.drawable.bg_image_t
            'u', 'U' -> R.drawable.bg_image_u
            'v', 'V' -> R.drawable.bg_image_v
            'w', 'W' -> R.drawable.bg_image_w
            'x', 'X' -> R.drawable.bg_image_x
            'y', 'Y' -> R.drawable.bg_image_y
            'z', 'Z' -> R.drawable.bg_image_z
            '0' -> R.drawable.bg_image_0
            '1' -> R.drawable.bg_image_1
            '2' -> R.drawable.bg_image_2
            '3' -> R.drawable.bg_image_3
            '4' -> R.drawable.bg_image_4
            '5' -> R.drawable.bg_image_5
            '6' -> R.drawable.bg_image_6
            '7' -> R.drawable.bg_image_7
            '8' -> R.drawable.bg_image_8
            '9' -> R.drawable.bg_image_9
            else -> R.drawable.ic_launcher_background // Default placeholder
        }
    }
}