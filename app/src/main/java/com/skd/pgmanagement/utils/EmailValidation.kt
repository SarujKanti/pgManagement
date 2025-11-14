package com.skd.pgmanagement.utils

import android.graphics.Typeface
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.widget.EditText
import android.widget.ImageView
import com.skd.pgmanagement.services.AlphabetImageMapper
import java.util.regex.Pattern

object EmailValidation {
    fun isEmailValid(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }


    class IFSCInputFilter : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            val regex = Regex("^[A-Z]{0,4}[0-9]{0,7}$")
            val newText = dest.toString().substring(0, dstart) + source.toString() + dest.toString().substring(dend)
            return if (newText.matches(regex)) {
                null
            } else {
                ""
            }
        }
    }

     fun setupIfscEditText(editText: EditText) {
        editText.filters = arrayOf(IFSCInputFilter(), InputFilter.LengthFilter(11))
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val length = s?.length ?: 0
                when {
                    length < 4 -> {
                        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                    }
                    length in 4..10 -> {
                        editText.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                }
                editText.setSelection(length)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    class PanInputFilter : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            val regex = Regex("^[A-Z]{0,5}[0-9]{0,4}[A-Z]?$")
            val newText = dest.toString().substring(0, dstart) + source.toString() + dest.toString().substring(dend)
            return if (newText.matches(regex)) {
                null
            } else {
                ""
            }
        }
    }

     fun setupPanEditText(editText: EditText) {
        editText.filters = arrayOf(PanInputFilter(), InputFilter.LengthFilter(10))
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val length = s?.length ?: 0
                when {
                    length < 5 -> {
                        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                    }
                    length in 5..8 -> {
                        editText.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                    length == 9 -> {
                        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                    }
                }
                editText.setSelection(length)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun formatPhoneNumber(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            var isFormatting: Boolean = false
            var deletingSpace: Boolean = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (count > 0 && s?.length == 6 && s[start] == ' ') {
                    deletingSpace = true
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting || s == null) {
                    return
                }
                isFormatting = true

                val cleanText = s.toString().replace(" ", "")
                val formattedText = StringBuilder()

                for (i in cleanText.indices) {
                    if (i == 5) {
                        formattedText.append(" ")
                    }
                    formattedText.append(cleanText[i])
                }

                editText.removeTextChangedListener(this)
                editText.setText(formattedText.toString())
                editText.setSelection(formattedText.length)
                editText.addTextChangedListener(this)

                isFormatting = false
            }
        })
    }

    fun setImageForName(name: String, imageView: ImageView) {
        val firstChar = name.getOrNull(0)?.uppercaseChar() ?: ' '
        val imageResource = AlphabetImageMapper.getImageResourceForLetter(firstChar)
        imageView.setImageResource(imageResource)
    }

    fun getBoldSpannable(text: String): SpannableString {
        val pattern = Pattern.compile("\\*(.*?)\\*")
        val matcher = pattern.matcher(text)
        val cleanText = text.replace("\\*(.*?)\\*".toRegex(), "$1")
        val spannable = SpannableString(cleanText)

        var offset = 0
        while (matcher.find()) {
            val matchStart = matcher.start()
            val matchEnd = matcher.end()
            val boldTextLength = matcher.group(1)?.length ?: 0

            val start = matchStart - offset
            val end = start + boldTextLength

            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            offset += 2 // 1 for each '*'
        }

        return spannable
    }

}
