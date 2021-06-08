package com.google.assistant.actions

import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.shortcuts.*

fun convertActionsToShortcuts(actions: ActionsRoot): ShortcutsRoot {
    val capabilityConverter = CapabilityConverter()
    val shortcutConverter = ShortcutConverter()
    return ShortcutsRoot(capabilityConverter.convertActionsToCapabilities(actions), shortcutConverter.convertActionsToShortcuts(actions))
}
