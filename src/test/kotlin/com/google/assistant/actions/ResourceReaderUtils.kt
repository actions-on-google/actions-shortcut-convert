package com.google.assistant.actions

import com.google.assistant.actions.marshall.readActions
import com.google.assistant.actions.marshall.readShortcuts
import com.google.assistant.actions.model.actions.ActionsRoot
import com.google.assistant.actions.model.shortcuts.ShortcutsRoot

class ResourceReaderUtils {

    fun readActionsResource(actionsFilePath: String): ActionsRoot {
        val actionsStream = this.javaClass.getResourceAsStream(actionsFilePath)
        return readActions(actionsStream)
    }

    fun readShortcutsResource(shortcutsFilePath: String): ShortcutsRoot {
        val shortcutsStream = this.javaClass.getResourceAsStream(shortcutsFilePath)
        return readShortcuts(shortcutsStream)
    }
}
