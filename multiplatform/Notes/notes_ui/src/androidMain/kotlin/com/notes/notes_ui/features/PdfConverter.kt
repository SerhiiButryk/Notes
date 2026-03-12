package com.notes.notes_ui.features

import android.content.Context
import android.net.Uri
import android.os.CancellationSignal
import android.print.PageRange
import android.print.PrintAttributes
import android.print.createLayoutCallback
import android.print.createWriteCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.documentfile.provider.DocumentFile
import api.PlatformAPIs.logger

class PdfConverter {

    fun convertHtmlToPdf(context: Context, htmlString: String, fileName: String, folderUri: Uri) {

        val rootFolder = DocumentFile.fromTreeUri(context, folderUri)
        val newFile = rootFolder?.createFile("application/pdf", fileName)

        if (newFile == null) {
            logger.loge("PdfConverter::convertHtmlToPdf failed to create a new file")
            return
        }

        // WenView is a local variable. Seems like it works.
        // Also we could try to make it a member variable.
        val webView = WebView(context)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if (view != null) {
                    genPdf(view, newFile.uri, fileName, context)
                } else {
                    logger.loge("PdfConverter::convertHtmlToPdf webview is null")
                }
            }
        }

        webView.settings.javaScriptEnabled = false
        webView.settings.blockNetworkImage = true
        webView.settings.blockNetworkLoads = true

        // Load the HTML content
        webView.loadDataWithBaseURL(null, htmlString, "text/html", "UTF-8", null)
    }

    private fun genPdf(webView: WebView, outputFile: Uri, fileName: String, context: Context) {

        logger.logi("PdfConverter::genPdf started")

        val printAdapter = webView.createPrintDocumentAdapter(fileName)

        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        val cancelSignal = CancellationSignal()

        val fileDescriptor = context.contentResolver.openFileDescriptor(outputFile, "rwt")

        val writeResultCallback = createWriteCallback(
            onFinished = {
                logger.logi("PdfConverter::genPdf onWrite success")
                fileDescriptor?.close()
            },
            onFailed = { error ->
                logger.logi("PdfConverter::genPdf onWrite failed - $error")
                fileDescriptor?.close()
            }
        )

        val layoutCallback = createLayoutCallback(
            onFinished = { _, _ ->
                logger.logi("PdfConverter::genPdf onLayout success")

                printAdapter.onWrite(
                    arrayOf(PageRange.ALL_PAGES),
                    fileDescriptor,
                    cancelSignal,
                    writeResultCallback)
            },
            onFailed = {
                logger.logi("PdfConverter::genPdf onLayout failed")
                fileDescriptor?.close()
            }
        )

        printAdapter.onLayout(
            null,
            printAttributes,
            null,
            layoutCallback,
            null
        )

    }
}