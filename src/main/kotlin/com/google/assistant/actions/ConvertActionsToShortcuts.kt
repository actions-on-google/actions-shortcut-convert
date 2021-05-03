package com.google.assistant.actions

import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.shortcuts.*

fun convertActionsToShortcuts(actions: ActionsRoot): ShortcutsRoot {
    val capabilities = actions.actions.map { action ->
        Capability(
            name = action.intentName,
            intent = CapabilityIntent(
                urlTemplate = UrlTemplate(action.fulfillment!!.urlTemplate!!)
            )
        )
    }
    val shortcuts = listOf<Shortcut>()

    return ShortcutsRoot(capabilities, shortcuts)
}