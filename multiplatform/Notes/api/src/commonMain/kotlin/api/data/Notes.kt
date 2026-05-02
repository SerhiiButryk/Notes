package api.data

data class Notes(
    val content: String = "",
    val id: Long = 0,
    val userId: String = "",
    val time: String = ""
) {
    companion object {
        fun NewNote() = Notes(id = -1)

        fun AbsentNote() = Notes(id = -2)
    }
}

fun Notes.getStringRep(): String {
    return content
}

fun String.toNote(id: Long): Notes {
    return Notes(content = this, id = id, userId = "", time = "")
}

fun List<Notes>.isEqualTo(list: List<Notes>): Boolean {
    if (size != list.size) {
        return false
    }
    forEach { item ->

        val found = list.find { it.id == item.id }

        if (found == null)
            return false

        val identical = found.content == item.content && found.userId == item.userId

        if (!identical)
            return false
    }
    return true
}