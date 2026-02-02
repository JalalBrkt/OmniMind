package com.omnimind.pro.final.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun MarkdownText(text: String, modifier: Modifier = Modifier, color: Color) {
    val styled = buildAnnotatedString {
        var bold = false
        var italic = false
        var i = 0
        while (i < text.length) {
            if (text.startsWith("**", i)) {
                bold = !bold
                i += 2
            } else if (text.startsWith("*", i)) {
                italic = !italic
                i += 1
            } else {
                var nextSpecial = text.length
                val nextBold = text.indexOf("**", i)
                val nextItalic = text.indexOf("*", i)

                if (nextBold != -1 && nextBold < nextSpecial) nextSpecial = nextBold
                if (nextItalic != -1 && nextItalic < nextSpecial) nextSpecial = nextItalic

                val content = text.substring(i, nextSpecial)
                pushStyle(SpanStyle(
                    fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
                    color = color
                ))
                append(content)
                pop()
                i = nextSpecial
            }
        }
    }
    Text(text = styled, modifier = modifier)
}
