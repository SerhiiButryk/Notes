package com.notes.notes_ui.features

import api.data.Notes

fun List<Notes>.toHtml(): String {

    // To separate on pages
    var htmlContent = """
                <style>
                  .page-break {
                    /* Standard property for modern browsers and PDF engines */
                    break-before: page;
                    
                    /* Legacy support for older PDF generators (like wkhtmltopdf) */
                    page-break-before: always;
                  }
                </style>
            """.trimIndent()

    forEach { notes ->
        htmlContent += notes.content
        // End of this page
        htmlContent += """
                    <div class="page-break"></div>
                """.trimIndent()
    }

    return htmlContent
}