package com.google.assistant.actions

import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.shortcuts.*

private const val ANDROID_ACTION_VIEW_DEFEAULT_INTENT = "android.intent.action.VIEW"
private const val ACTIONS_BUILT_IN_INTENT_RESERVED_NAMESPACE = "actions.intent"
private const val BII_INTENT_PARAMETER_MIME_TYPE = "text/*"

fun convertActionsToShortcuts(actions: ActionsRoot): ShortcutsRoot {
    val capabilityConverter = CapabilityConverter()

    val capabilities = actions.actions.map { action ->
        capabilityConverter.convertActionsToShortcuts(action)
    }

    val shortcuts = listOf<Shortcut>()

    return ShortcutsRoot(capabilities, shortcuts)
}
