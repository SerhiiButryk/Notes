package com.notes.notes_ui.editor

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import dev.mkeeda.arranger.richtext.AttributeContainer
import dev.mkeeda.arranger.richtext.AttributeKey
import dev.mkeeda.arranger.richtext.BoldKey
import dev.mkeeda.arranger.richtext.BulletListKey
import dev.mkeeda.arranger.richtext.HeadingKey
import dev.mkeeda.arranger.richtext.HeadingLevel
import dev.mkeeda.arranger.richtext.ItalicKey
import dev.mkeeda.arranger.richtext.ListIndentLevel
import dev.mkeeda.arranger.richtext.OrderedListKey
import dev.mkeeda.arranger.richtext.RichString
import dev.mkeeda.arranger.richtext.SpanAttributeKey
import dev.mkeeda.arranger.richtext.StrikethroughKey
import dev.mkeeda.arranger.richtext.TextAlignment
import dev.mkeeda.arranger.richtext.TextAlignmentKey
import dev.mkeeda.arranger.richtext.UnderlineKey
import dev.mkeeda.arranger.richtext.bold
import dev.mkeeda.arranger.richtext.bulletList
import dev.mkeeda.arranger.richtext.headingLevel
import dev.mkeeda.arranger.richtext.italic
import dev.mkeeda.arranger.richtext.orderedList
import dev.mkeeda.arranger.richtext.rangeOf
import dev.mkeeda.arranger.richtext.strikethrough
import dev.mkeeda.arranger.richtext.textAlignment
import dev.mkeeda.arranger.richtext.underline

class HtmlParser {
    private var richString = RichString(text = "")
    private var inRichList = false
    private var startListIndex = 0
    private var lastListIndex = 0
    private var attributes = AttributeContainer.empty()

    private fun prepare() {
        richString = RichString(text = "")
        inRichList = false
        startListIndex = 0
        lastListIndex = 0
        attributes = AttributeContainer.empty()
    }

    fun parse(html: String): RichString {
        prepare()

        if (html.isEmpty()) return richString

        val body = Ksoup.parseBodyFragment(html).body()

        // Start DOM traversal
        for (child in body.childNodes()) {
            // Parser rich chunk
            traverse(child)
        }

        if (attributes.isNotEmpty()) {
            // Finish editing
            richString = richString.editChunk()
            // Now should be empty
            require(attributes.isEmpty())
        }

        return richString
    }

    private fun traverse(node: Node) {
        when (node) {
            is TextNode -> {
                val text = node.text()
                if (text.isNotEmpty()) {
                    richString =
                        if (attributes.isNotEmpty()) {
                            richString.editChunk(text)
                        } else {
                            richString.newRichChunkWith(text)
                        }
                }
            }

            is Element -> {
                val tagName = node.tagName().lowercase()

                // Handle line breaks
                if (tagName == "br") {
                    richString = richString.newRichChunkWith("\n")
                    return
                }

                // Check for inline formatting tags
                attributes = updateAttributeContainer(attributes, tagName, node)

                // Recursively traverse child nodes
                for (child in node.childNodes()) {
                    traverse(child)
                }
            }
        }
    }

    private fun RichString.newRichChunkWith(text: String): RichString = RichString(text = this.text + text, spans = this.spans)

    private fun RichString.editChunk(newText: String? = null): RichString {
        // Add list entries
        if (attributes.containsKey(ListEntryKey)) {
            // Text should be set
            require(newText != null)

            val listEntryText = "$newText\n"
            val newRichString = newRichChunkWith(listEntryText)

            val range = newRichString.text.rangeOf(listEntryText)
            if (!inRichList) {
                startListIndex = range.first
                inRichList = true
            }

            lastListIndex = range.last

            attributes = attributes.minus(ListEntryKey)

            return newRichString
        }

        // Finish list entries
        if (attributes.containsKey(BulletListKey) ||
            attributes.containsKey(OrderedListKey)
        ) {
            richString =
                richString.edit {
                    editAttributes(startListIndex until lastListIndex) {
                        ifAttributeKey(OrderedListKey) {
                            bulletList(ListIndentLevel.Level1)
                        }
                        ifAttributeKey(BulletListKey) {
                            bulletList(ListIndentLevel.Level1)
                        }
                    }
                }
        }

        // Text should be set
        require(newText != null)

        // Handle simple inline tags
        richString = richString.newRichChunkWith(newText)
        val range = richString.text.rangeOf(newText)
        return richString.edit {
            editAttributes(range = range) {
                ifAttributeKey(BoldKey) {
                    bold()
                }
                ifAttributeKey(ItalicKey) {
                    italic()
                }
                ifAttributeKey(UnderlineKey) {
                    underline()
                }
                ifAttributeKey(StrikethroughKey) {
                    strikethrough()
                }
                ifAttributeKey(HeadingKey) {
                    val value = attributes[HeadingKey]
                    headingLevel(value)
                }
                ifAttributeKey(TextAlignmentKey) {
                    val value = attributes[TextAlignmentKey]
                    textAlignment(value)
                }
            }
        }
    }

    private fun <T> ifAttributeKey(
        key: AttributeKey<T>,
        block: () -> Unit,
    ) {
        if (attributes.containsKey(key)) {
            block()
            attributes = attributes.minus(key)
        }
    }

    private fun updateAttributeContainer(
        current: AttributeContainer,
        tagName: String,
        node: Node,
    ): AttributeContainer {
        when (tagName) {
            "b", "strong" -> {
                return current.plus(pair = Pair(BoldKey, Unit))
            }

            "i", "em" -> {
                return current.plus(pair = Pair(ItalicKey, Unit))
            }

            "u", "ins" -> {
                return current.plus(pair = Pair(UnderlineKey, Unit))
            }

            "s", "strike", "del" -> {
                return current.plus(pair = Pair(StrikethroughKey, Unit))
            }

            "h1" -> {
                return current.plus(pair = Pair(HeadingKey, HeadingLevel.H1))
            }

            "h2" -> {
                return current.plus(pair = Pair(HeadingKey, HeadingLevel.H2))
            }

            "h3" -> {
                return current.plus(pair = Pair(HeadingKey, HeadingLevel.H3))
            }

            "h4" -> {
                return current.plus(pair = Pair(HeadingKey, HeadingLevel.H4))
            }

            "h5" -> {
                return current.plus(pair = Pair(HeadingKey, HeadingLevel.H5))
            }

            "h6" -> {
                return current.plus(pair = Pair(HeadingKey, HeadingLevel.H6))
            }

            "div" -> {
                val textAlign = node.attributes().attribute("style")
                if (textAlign != null) {
                    val type =
                        when {
                            textAlign.value.contains(
                                "text-align: center",
                                ignoreCase = true,
                            ) -> TextAlignment.Center

                            textAlign.value.contains(
                                "text-align: right",
                                ignoreCase = true,
                            ) -> TextAlignment.Right

                            textAlign.value.contains(
                                "text-align: left",
                                ignoreCase = true,
                            ) -> TextAlignment.Left

                            else -> TextAlignment.Justify
                        }
                    return current.plus(TextAlignmentKey, type)
                }
                return current
            }

            "ul" -> {
                return current.plus(BulletListKey, ListIndentLevel.Level1)
            }

            "ol" -> {
                return current.plus(OrderedListKey, ListIndentLevel.Level1)
            }

            "li" -> {
                return current.plus(ListEntryKey, Unit)
            }

            else -> {
                return current
            }
        }
    }
}

object ListEntryKey : SpanAttributeKey<Unit> {
    override val name: String
        get() = ""
    override val defaultValue: Unit
        get() = Unit
}
