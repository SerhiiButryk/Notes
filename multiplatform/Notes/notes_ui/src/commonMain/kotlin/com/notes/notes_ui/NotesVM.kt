package com.notes.notes_ui

import api.Platform
import api.repo.Repository
import kotlinx.coroutines.CoroutineScope

class NotesVM(
    repository: Repository = Platform().appRepo,
    // For test support
    scopeOverride: CoroutineScope? = null,
) : NotesVMBase(scopeOverride, repository) {

}
