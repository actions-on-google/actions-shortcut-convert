package com.google.assistant.actions

import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.shortcuts.*

fun convertActionsToShortcuts(actions: ActionsRoot): ShortcutsRoot {
    val capabilityConverter = CapabilityConverter()
    val shortcutConverter = ShortcutConverter()

    val capabilities = actions.actions.map { action ->
        capabilityConverter.convertActionsToShortcuts(action)
    }
    return ShortcutsRoot(capabilities, shortcutConverter.convertActionsToShortcuts(actions))
}
