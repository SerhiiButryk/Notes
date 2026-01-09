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

        fun DeletedNote() = Notes(id = -3)
    }
}
