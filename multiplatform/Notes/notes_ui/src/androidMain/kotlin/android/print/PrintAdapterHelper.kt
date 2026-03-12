package android.print

/**
* To workaround hidden constructor issue (@UnsupportedAppUsage annotation)
*/

fun createLayoutCallback(
    onFinished: (info: PrintDocumentInfo?, changed: Boolean) -> Unit,
    onFailed: (error: CharSequence?) -> Unit
): PrintDocumentAdapter.LayoutResultCallback {
    return object : PrintDocumentAdapter.LayoutResultCallback() {
        override fun onLayoutFinished(info: PrintDocumentInfo?, changed: Boolean) {
            onFinished(info, changed)
        }
        override fun onLayoutFailed(error: CharSequence?) {
            onFailed(error)
        }
    }
}

fun createWriteCallback(
    onFinished: (pages: Array<out PageRange>?) -> Unit,
    onFailed: (error: CharSequence?) -> Unit
): PrintDocumentAdapter.WriteResultCallback {
    return object : PrintDocumentAdapter.WriteResultCallback() {
        override fun onWriteFinished(pages: Array<out PageRange>?) {
            onFinished(pages)
        }
        override fun onWriteFailed(error: CharSequence?) {
            onFailed(error)
        }
    }
}
