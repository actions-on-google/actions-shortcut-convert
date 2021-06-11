package com.google.assistant.actions

import com.google.assistant.actions.converters.convertActionsToCapabilities
import com.google.assistant.actions.converters.convertActionsToShortcuts
import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.shortcuts.ShortcutsRoot

fun convertActionsToShortcuts(actions: ActionsRoot): ShortcutsRoot {
    return ShortcutsRoot(convertActionsToCapabilities(actions), convertActionsToShortcuts(actions))
}
