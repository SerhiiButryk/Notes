package com.notes.notes_ui.editor

import dev.mkeeda.arranger.richtext.AttributeContainer
import dev.mkeeda.arranger.richtext.BoldKey
import dev.mkeeda.arranger.richtext.BulletListKey
import dev.mkeeda.arranger.richtext.HeadingKey
import dev.mkeeda.arranger.richtext.HeadingLevel
import dev.mkeeda.arranger.richtext.ItalicKey
import dev.mkeeda.arranger.richtext.OrderedListKey
import dev.mkeeda.arranger.richtext.StrikethroughKey
import dev.mkeeda.arranger.richtext.TextAlignment
import dev.mkeeda.arranger.richtext.TextAlignmentKey
import dev.mkeeda.arranger.richtext.UnderlineKey
import dev.mkeeda.arranger.richtext.editor.RichTextState

fun RichTextState.toHtml(): String {
    fun escapeSpecialChars(chunkText: String): String =
        chunkText
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\n", "<br/>")

    val builder = StringBuilder()

    val text = this.richString.text
    val attributes = this.richString.runs { true }

    var chunkStart = 0

    var inOrderedList = false
    var inBulletList = false

    for (attr in attributes) {
        val chunkText = attr.text
        if (chunkText.isEmpty()) continue

        val chunkEnd = attr.range.first
        if ((chunkEnd - chunkStart) > 0) {
            // Append text with no styles
            val chunk = text.substring(chunkStart, chunkEnd)
            val formattedChunk = escapeSpecialChars(chunk)
            builder.append(formattedChunk)
        }

        chunkStart = attr.range.last + 1

        val container: AttributeContainer = attr.value

        // Set list open tag

        if (container.containsKey(OrderedListKey)) {
            inOrderedList = true
            builder.append("<ol>")
        }

        if (container.containsKey(BulletListKey)) {
            inBulletList = true
            builder.append("<ul>")
        }

        // Add individual list entries
        //  are separated by '\n' char like that
        // "item 1\nitem 2\nitem 3\n"
        if (inOrderedList || inBulletList) {
            var start = 0
            var end = chunkText.indexOf('\n')

            while (end != -1) {
                val chunk = chunkText.substring(startIndex = start, endIndex = end)

                val safeChunk = escapeSpecialChars(chunk)
                val outText = formatChunk(safeChunk, container, inOrderedList, inBulletList)
                builder.append(outText)

                // Move to the next list entry
                start = end + 1
                end = chunkText.indexOf(startIndex = start, char = '\n')
            }

            if (inBulletList) {
                inBulletList = false
                builder.append("</ul>")
            }

            if (inOrderedList) {
                inOrderedList = false
                builder.append("</ol>")
            }

            // Done!
            continue
        }

        // Process other elements
        val safeChunk = escapeSpecialChars(chunkText)
        val outText = formatChunk(safeChunk, container)
        builder.append(outText)
    }

    // There is remaining text which doesn't have formating,
    // but we must add it to the builder
    if (this.richString.text.length - chunkStart > 0) {
        val remaining = richString.text.substring(chunkStart)
        builder.append(remaining)
    }

    return builder.toString()
}

private fun formatChunk(
    chunk: String,
    container: AttributeContainer,
    inOrderedList: Boolean = false,
    inBulletList: Boolean = false,
): String {
    var formattedChunk = chunk

    // Style conversion

    if (container.containsKey(BoldKey)) {
        formattedChunk = "<b>$formattedChunk</b>"
    }

    if (container.containsKey(ItalicKey)) {
        formattedChunk = "<i>$formattedChunk</i>"
    }

    if (container.containsKey(UnderlineKey)) {
        formattedChunk = "<u>$formattedChunk</u>"
    }

    if (container.containsKey(StrikethroughKey)) {
        formattedChunk = "<s>$formattedChunk</s>"
    }

    // H conversion

    if (container.containsKey(HeadingKey)) {
        val value = container[HeadingKey]
        if (value == HeadingLevel.H1) {
            formattedChunk = "<h1>$formattedChunk</h1>"
        }
        if (value == HeadingLevel.H2) {
            formattedChunk = "<h2>$formattedChunk</h2>"
        }
        if (value == HeadingLevel.H3) {
            formattedChunk = "<h3>$formattedChunk</h3>"
        }
        if (value == HeadingLevel.H4) {
            formattedChunk = "<h4>$formattedChunk</h4>"
        }
        if (value == HeadingLevel.H5) {
            formattedChunk = "<h5>$formattedChunk</h5>"
        }
        if (value == HeadingLevel.H6) {
            formattedChunk = "<h6>$formattedChunk</h6>"
        }
    }

    // Alignment conversion

    if (container.containsKey(TextAlignmentKey)) {
        val value = container[TextAlignmentKey]
        val alignValue =
            when (value) {
                TextAlignment.Center -> "center"
                TextAlignment.Right -> "right"
                TextAlignment.Left -> "left"
                else -> "justify"
            }
        formattedChunk = "<div style=\"text-align: $alignValue;\">$formattedChunk</div>"
    }

    // List entry

    if (inOrderedList || inBulletList) {
        formattedChunk = "<li>$formattedChunk</li>"
    }

    return formattedChunk
}
